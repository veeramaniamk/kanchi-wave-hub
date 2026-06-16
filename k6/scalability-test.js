import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '2s', target: 500 },  // instant spike to 500 VUs
    { duration: '10s', target: 500 }, // sustain spike
    { duration: '3s', target: 0 },    // scale down
  ],
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  const email = `k6_scale_${Date.now()}@example.com`;
  const registerPayload = JSON.stringify({
    name: 'K6 Scaler',
    email: email,
    password: 'password123',
    phone: 9999000033,
    address: 'K6 Scalability Test Street',
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
  
  const loginRes = http.post(`${BASE_URL}/api/login`, loginPayload, { headers });
  const loginSuccess = check(loginRes, {
    'login success': (r) => r.status === 200,
  });

  if (loginSuccess) {
    const token = loginRes.json().token;
    const authHeaders = {
      'Authorization': `Bearer ${token}`,
    };

    // Get orders to put pressure on database joins and connections
    const ordersRes = http.get(`${BASE_URL}/api/get_orders?status=pending`, { headers: authHeaders });
    check(ordersRes, {
      'orders query executed': (r) => r.status === 200 || r.status === 404,
    });
  }

  sleep(0.2);
}
