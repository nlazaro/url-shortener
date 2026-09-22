// Load test for the redirect endpoint against a large, pre-seeded table.
// Run loadtest/seed.sql first (see instructions), then:
//   k6 run loadtest/redirect-seeded.js
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const SEED_COUNT = 3000000; // must match generate_series() upper bound in seed.sql

export const options = {
  vus: 50,
  duration: '30s',
  maxRedirects: 0,
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

function seedCode(i) {
  return 'sd' + String(i).padStart(9, '0');
}

export default function () {
  // Uniform random index across the full seeded range, so lookups are spread
  // across the whole table rather than clustering on a few hot rows.
  const i = Math.floor(Math.random() * SEED_COUNT) + 1;
  const res = http.get(`${BASE_URL}/r/${seedCode(i)}`);
  check(res, { 'status is 302': (r) => r.status === 302 });
}
