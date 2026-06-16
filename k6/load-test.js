import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '5s', target: 50 },   // ramp up to 50 users
    { duration: '5s', target: 100 },  // ramp up to 100 users
    { duration: '5s', target: 500 },  // ramp up to 500 users
    { duration: '5s', target: 1000 }, // ramp up to 1000 users
    { duration: '5s', target: 5000 }, // ramp up to 5000 users
    { duration: '5s', target: 0 },    // scale down to 0
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'], // less than 1% errors
    http_req_duration: ['p(95)<500'], // p95 response time must be under 500ms
  },
};

const BASE_URL = 'http://localhost:8080';

// Setup phase: register a user to test login with
export function setup() {
  const email = `k6_load_${Date.now()}@example.com`;
  const registerPayload = JSON.stringify({
    name: 'K6 Loader',
    email: email,
    password: 'password123',
    phone: 9999000011,
    address: 'K6 Load Test Street',
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
  
  // 1. Login to retrieve token
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

    // 2. Fetch Profile
    const profileRes = http.get(`${BASE_URL}/api/profile`, { headers: authHeaders });
    check(profileRes, {
      'profile loaded': (r) => r.status === 200,
    });

    // 3. Fetch Saree Catalog
    const catalogRes = http.get(`${BASE_URL}/api/fetch_product?page=0&size=5`, { headers: authHeaders });
    check(catalogRes, {
      'catalog loaded': (r) => r.status === 200,
    });
  }

  sleep(0.5); // wait 500ms between loops
}
