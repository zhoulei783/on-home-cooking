<template>
  <div class="login-choice">
    <div class="card">
      <h1 class="title">🍳 上门烹饪服务平台</h1>
      <p class="subtitle">让美食走进您的家门</p>
      <h2>选择登录方式</h2>
      <div class="choice-grid">
        <div class="choice-item" @click="$router.push('/login')">
          <span class="choice-icon">👤</span>
          <span class="choice-text">用户登录</span>
        </div>
        <div class="choice-item" @click="$router.push('/chef-login')">
          <span class="choice-icon">👨‍🍳</span>
          <span class="choice-text">厨师登录</span>
        </div>
        <div class="choice-item" @click="$router.push('/admin-login-page')">
          <span class="choice-icon">🔧</span>
          <span class="choice-text">管理员登录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginChoice'
}
</script>

<style scoped>
.login-choice {
  max-width: 600px;
  margin: 50px auto;
}

.card {
  text-align: center;
}

.title {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

h2 {
  margin-bottom: 20px;
}

.choice-grid {
  display: flex;
  gap: 20px;
  margin-top: 30px;
  justify-content: center;
}

.choice-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background: #f5f7fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.choice-item:hover {
  background: #ecf5ff;
  transform: translateY(-5px);
}

.choice-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.choice-text {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.back-link a {
  color: #409eff;
  text-decoration: none;
}
</style>