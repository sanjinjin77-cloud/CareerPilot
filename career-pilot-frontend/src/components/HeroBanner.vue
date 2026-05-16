<template>
  <section class="hero-shell">
    <div class="hero-bg-decoration">
      <span class="hero-orbit hero-orbit--one"></span>
      <span class="hero-orbit hero-orbit--two"></span>
      <span class="hero-flag">Offer</span>
    </div>

    <div class="hero-content">
      <div class="hero-left">
        <p class="eyebrow">AI Career Growth Agent</p>
        <h1>从入学到 Offer，CareerPilot 陪你一路通关</h1>
        <p class="hero-banner__subtitle">
          基于大语言模型、用户画像与岗位能力模型，为大学生提供职业规划、能力提升、简历优化、JD
          匹配和模拟面试的全周期成长陪跑服务。
        </p>
        <div class="hero-banner__actions">
          <router-link class="cp-button cp-button--primary" to="/profile">开始职业测评</router-link>
          <router-link class="cp-button cp-button--ghost" to="/planning">查看我的成长路线</router-link>
        </div>
      </div>

      <div class="hero-right" aria-label="AI 成长控制台">
        <div class="growth-console">
          <div class="growth-console__header">
            <span class="assistant-card__icon">AI</span>
            <div>
              <strong>AI 成长控制台</strong>
              <small>正在规划你的职业通关路线</small>
            </div>
          </div>

          <div class="console-route">
            <span class="done">画像</span>
            <span class="done">能力</span>
            <span class="current">简历</span>
            <span>面试</span>
            <span>Offer</span>
          </div>

          <div class="console-message">
            建议本周补强 TypeScript，并把课程项目升级为可展示的工程化项目。
          </div>
        </div>

        <div class="floating-card match-card">
          <small>岗位匹配度</small>
          <strong>72%</strong>
          <span>较上周 +8%</span>
        </div>

        <div class="floating-card progress-card">
          <small>通关进度</small>
          <strong>3 / 6</strong>
          <div class="mini-progress"><span></span></div>
        </div>

        <div class="offer-badge">
          <span>终点</span>
          <strong>Offer 冲刺</strong>
        </div>
      </div>
    </div>
  </section>
</template>
