import http from 'k6/http';
import { check, sleep } from 'k6';

// Read duration from environment variable, defaulting to 15s for quick verification
const testDuration = __ENV.DURATION || '15s';

export const options = {
  vus: 100, // constant 100 users
  duration: testDuration,
  thresholds: {
    http_req_failed: ['rate<0.005'], // less than 0.5% failures over long duration
  },
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
  const email = `k6_reliability_${Date.now()}@example.com`;
  const registerPayload = JSON.stringify({
    name: 'K6 Reliabler',
    email: email,
    password: 'password123',
    phone: 9999000044,
    address: 'K6 Reliability Test Street',
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

    // Constant catalog queries to test memory leak and cache eviction
    const catalogRes = http.get(`${BASE_URL}/api/fetch_product?page=0&size=10`, { headers: authHeaders });
    check(catalogRes, {
      'catalog fetch successful': (r) => r.status === 200,
    });
  }

  sleep(0.3);
}
