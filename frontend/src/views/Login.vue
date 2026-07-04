<template>
  <div class="login">
    <div class="card">
      <h2>用户登录</h2>
      <form @submit.prevent="login">
        <div class="form-item">
          <label for="phone">手机号：</label>
          <input type="text" id="phone" name="phone" v-model="phone" placeholder="请输入测试手机号：13800138000" autocomplete="tel">
        </div>
        <div class="form-item">
          <label for="password">密码：</label>
          <input type="password" id="password" name="password" v-model="pwd" placeholder="请输入测试密码：123456" autocomplete="current-password">
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </form>
      <div class="back-link">
        <router-link to="/register">还没有账号？立即注册</router-link>
      </div>
      <div class="back-link">
        <router-link to="/">返回首页</router-link>
      </div>
      <div :class="['result', result.type]" v-if="result.message">
        {{ result.message }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      phone: '13800138000',
      pwd: '123456',
      loading: false,
      result: { type: '', message: '' }
    }
  },
  methods: {
    async login() {
      if (!this.phone || !this.pwd) {
        this.result = { type: 'error', message: '请输入手机号和密码' }
        return
      }

      this.loading = true
      this.result = { type: '', message: '' }

      try {
        const res = await this.$axios.post('/user/login', {
          phone: this.phone,
          password: this.pwd
        })

        if (res.data.code === 200) {
          this.result = {
            type: 'success',
            message: `✅ 登录成功！欢迎 ${res.data.data.username}（用户ID：${res.data.data.id}）`
          }
          localStorage.setItem('user', JSON.stringify(res.data.data))
          this.$emit('login-success', res.data.data)
          setTimeout(() => {
            this.$router.push('/chefs')
          }, 1500)
        } else {
          this.result = { type: 'error', message: `❌ 登录失败：${res.data.msg}` }
        }
      } catch (err) {
        this.result = { type: 'error', message: `❌ 登录异常：${err.message}` }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: 0 auto;
}

.back-link {
  text-align: center;
  margin-top: 15px;
}

.back-link a {
  color: #409eff;
  text-decoration: none;
}
</style>