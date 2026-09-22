// Load test for the redirect endpoint.
// Run:  k6 run loadtest/redirect.js
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const NUM_CODES = 100;

export const options = {
  vus: 50,
  duration: '30s',
  maxRedirects: 0, // measure OUR redirect, not the destination site
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

// Runs once before the test: create a pool of short codes to hit
export function setup() {
  const codes = [];
  for (let i = 0; i < NUM_CODES; i++) {
    const res = http.post(
      `${BASE_URL}/shorten`,
      JSON.stringify({ url: `https://example.com/page/${i}` }),
      { headers: { 'Content-Type': 'application/json' } },
    );
    if (res.status !== 201) {
      throw new Error(`Setup failed: POST /shorten returned ${res.status}`);
    }
    codes.push(res.body);
  }
  return { codes };
}

export default function (data) {
  const code = data.codes[Math.floor(Math.random() * data.codes.length)];
  const res = http.get(`${BASE_URL}/r/${code}`);
  check(res, { 'status is 302': (r) => r.status === 302 });
}
