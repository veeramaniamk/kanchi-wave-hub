import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '5s', target: 100 },  // stress point 1
    { duration: '5s', target: 500 },  // stress point 2
    { duration: '5s', target: 2000 }, // stress point 3 (heavy stress)
    { duration: '5s', target: 8000 }, // breaking point search
    { duration: '5s', target: 0 },    // scale down
  ],
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  const email = `k6_stress_${Date.now()}@example.com`;
  const registerPayload = JSON.stringify({
    name: 'K6 Stresser',
    email: email,
    password: 'password123',
    phone: 9999000022,
    address: 'K6 Stress Test Street',
  });

  const headers = { 'Content-Type': 'application/json' };
  const res = http.post(`${BASE_URL}/api/register`, registerPayload, { headers });
  
  check(res, {
    'registered successfully': (r) => r.status === 200 || r.status === 409,
  });

  return { email };
}

export default function (data) {
  const loginPayload = JSON.stringify({
    email: data.email,
    password: 'password123',
  });

  const headers = { 'Content-Type': 'application/json' };
  
  // Login to retrieve token
  const loginRes = http.post(`${BASE_URL}/api/login`, loginPayload, { headers });
  const loginSuccess = check(loginRes, {
    'login success': (r) => r.status === 200,
  });

  if (loginSuccess) {
    const token = loginRes.json().token;
    const authHeaders = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    };

    // Fetch Profile
    const profileRes = http.get(`${BASE_URL}/api/profile`, { headers: authHeaders });
    check(profileRes, {
      'profile loaded': (r) => r.status === 200,
    });
  }

  sleep(0.1); // rapid requests to stress test the API
}
