<template>
  <div class="chef-login">
    <div class="card">
      <h2>厨师登录</h2>
      <form @submit.prevent="chefLogin">
        <div class="form-item">
          <label for="phone">手机号：</label>
          <input type="text" id="phone" v-model="phone" placeholder="请输入手机号" autocomplete="tel">
        </div>
        <div class="form-item">
          <label for="password">密码：</label>
          <input type="password" id="password" v-model="password" placeholder="请输入密码" autocomplete="current-password">
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '厨师登录' }}
        </button>
      </form>
      <div :class="['result', result.type]" v-if="result.message">
        {{ result.message }}
      </div>
      <div class="back-link">
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChefLogin',
  data() {
    return {
      phone: '',
      password: '',
      loading: false,
      result: { type: '', message: '' }
    }
  },
  methods: {
    async chefLogin() {
      if (!this.phone) {
        this.result = { type: 'error', message: '请输入手机号' }
        return
      }
      if (!this.password) {
        this.result = { type: 'error', message: '请输入密码' }
        return
      }

      this.loading = true
      this.result = { type: '', message: '' }

      try {
        const res = await this.$axios.post('/chef/login', {
          phone: this.phone,
          password: this.password
        })

        if (res.data.code === 200) {
          this.result = {
            type: 'success',
            message: `✅ 登录成功！欢迎 ${res.data.data.name}`
          }
          localStorage.setItem('chef', JSON.stringify(res.data.data))
          setTimeout(() => {
            this.$router.push('/chef-dashboard')
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
.chef-login {
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