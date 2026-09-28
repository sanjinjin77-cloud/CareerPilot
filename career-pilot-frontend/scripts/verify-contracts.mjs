import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { resolve } from 'node:path';

const root = resolve(fileURLToPath(new URL('..', import.meta.url)));

function read(path) {
  return readFileSync(resolve(root, path), 'utf8');
}

function assertContains(path, pattern, label) {
  const content = read(path);
  if (!content.includes(pattern)) {
    throw new Error(`${label}: expected ${path} to contain ${pattern}`);
  }
}

function assertNotContains(path, pattern, label) {
  const content = read(path);
  if (content.includes(pattern)) {
    throw new Error(`${label}: ${path} still contains ${pattern}`);
  }
}

assertContains('src/api/request.ts', 'error.response?.status === 401', '401 interceptor');
assertContains('src/api/request.ts', 'clearToken()', '401 token cleanup');
assertContains('src/api/request.ts', 'window.location.href', '401 login redirect');

assertContains('src/stores/auth.ts', 'getCurrentUserApi', 'auth state restoration');
assertContains('src/stores/auth.ts', 'this.logout()', 'invalid token cleanup');

assertContains('src/api/career.ts', 'decodeMaybeMojibake', 'career API mapper mojibake guard');
assertContains('src/api/dashboard.ts', 'decodeMaybeMojibake', 'dashboard API mapper mojibake guard');

assertContains('src/views/dashboard/DashboardView.vue', '当前显示演示数据', 'dashboard explicit fallback');
assertContains('src/views/ability/AbilityView.vue', '演示数据', 'ability explicit fallback');
assertContains('src/views/profile/ProfileView.vue', '当前显示演示数据', 'profile explicit fallback');

assertNotContains('src/views/planning/PlanningView.vue', 'getProfileApi().catch', 'planning silent profile fallback');
assertNotContains('src/views/planning/PlanningView.vue', 'getResumeMaterialsApi().catch', 'planning silent material fallback');
assertNotContains('src/views/ability/AbilityView.vue', '继续使用 mock', 'ability silent mock wording');
assertNotContains('src/views/profile/ProfileView.vue', 'mock 职业规划', 'profile mock wording');

console.log('Frontend contracts verified.');
