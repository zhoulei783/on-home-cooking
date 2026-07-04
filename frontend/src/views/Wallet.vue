<template>
  <div class="wallet-page">
    <div class="page-heading">
      <div>
        <p class="eyebrow">MY WALLET</p>
        <h1>我的余额</h1>
        <p class="heading-note">每一笔余额变化，都清清楚楚</p>
      </div>
      <button class="refresh-button" :disabled="loading" @click="loadBalanceDetails">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5M4 13a8.1 8.1 0 0 0 15.5 2M20 20v-5h-5"/>
        </svg>
        {{ loading ? '更新中' : '刷新' }}
      </button>
    </div>

    <div class="balance-card">
      <div class="balance-glow"></div>
      <div class="balance-main">
        <span class="balance-label">可用余额</span>
        <div class="balance-value"><small>¥</small>{{ money(summary.balance) }}</div>
        <span class="account-name">{{ username }}的账户</span>
      </div>
      <button class="recharge-button" @click="openRecharge">
        <span>+</span> 充值余额
      </button>
      <div class="card-mark">食</div>
    </div>

    <div class="summary-grid">
      <div class="summary-card income">
        <div class="summary-icon">
          <svg viewBox="0 0 24 24"><path d="M12 4v16m0 0 6-6m-6 6-6-6"/></svg>
        </div>
        <div>
          <span>累计入账</span>
          <strong>+ ¥{{ money(summary.totalIncome) }}</strong>
        </div>
      </div>
      <div class="summary-card expense">
        <div class="summary-icon">
          <svg viewBox="0 0 24 24"><path d="M12 20V4m0 0 6 6m-6-6-6 6"/></svg>
        </div>
        <div>
          <span>累计支出</span>
          <strong>- ¥{{ money(summary.totalExpense) }}</strong>
        </div>
      </div>
    </div>

    <section class="details-card">
      <div class="details-header">
        <div>
          <h2>余额明细</h2>
          <p>共 {{ filteredTransactions.length }} 笔记录</p>
        </div>
        <div class="filters" role="tablist" aria-label="流水筛选">
          <button
            v-for="item in filters"
            :key="item.value"
            :class="{ active: activeFilter === item.value }"
            @click="activeFilter = item.value"
          >{{ item.label }}</button>
        </div>
      </div>

      <div v-if="loading" class="state-box">
        <div class="loading-ring"></div>
        <p>正在整理余额明细...</p>
      </div>
      <div v-else-if="error" class="state-box error-state">
        <p>{{ error }}</p>
        <button @click="loadBalanceDetails">重新加载</button>
      </div>
      <div v-else-if="filteredTransactions.length === 0" class="state-box">
        <div class="empty-icon">¥</div>
        <h3>暂无余额记录</h3>
        <p>充值或支付订单后，记录会出现在这里</p>
      </div>
      <div v-else class="transaction-list">
        <article v-for="item in filteredTransactions" :key="item.id" class="transaction-item">
          <div :class="['transaction-icon', item.type]">
            <svg v-if="item.type === 'payment'" viewBox="0 0 24 24">
              <path d="M4 7h16v11H4zM4 10h16M16 15h1"/>
            </svg>
            <svg v-else-if="item.type === 'refund'" viewBox="0 0 24 24">
              <path d="M4 9h11a5 5 0 0 1 0 10h-3M4 9l4-4M4 9l4 4"/>
            </svg>
            <svg v-else viewBox="0 0 24 24">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </div>
          <div class="transaction-content">
            <div class="transaction-title-row">
              <h3>{{ item.title }}</h3>
              <strong :class="amountClass(item)">
                {{ amountPrefix(item) }}¥{{ money(item.amount) }}
              </strong>
            </div>
            <p>{{ item.description }}</p>
            <div class="transaction-meta">
              <span>{{ formatTime(item.createTime) }}</span>
              <span v-if="item.transactionId">流水号 {{ item.transactionId }}</span>
              <span v-if="item.balanceAfter !== null && item.balanceAfter !== undefined">
                余额 ¥{{ money(item.balanceAfter) }}
              </span>
              <span v-if="item.status !== 'success'" :class="['status-tag', item.status]">
                {{ statusText(item.status) }}
              </span>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Wallet',
  data() {
    return {
      loading: true,
      error: '',
      username: '用户',
      activeFilter: 'all',
      filters: [
        { label: '全部', value: 'all' },
        { label: '收入', value: 'income' },
        { label: '支出', value: 'expense' }
      ],
      summary: {
        balance: 0,
        totalIncome: 0,
        totalExpense: 0,
        transactions: []
      }
    }
  },
  computed: {
    filteredTransactions() {
      if (this.activeFilter === 'income') {
        return this.summary.transactions.filter(item => Number(item.changeAmount) > 0)
      }
      if (this.activeFilter === 'expense') {
        return this.summary.transactions.filter(item => Number(item.changeAmount) < 0)
      }
      return this.summary.transactions
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      this.$router.push('/login')
      return
    }
    this.username = user.username || '用户'
    this.loadBalanceDetails()
  },
  methods: {
    async loadBalanceDetails() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) return
      this.loading = true
      this.error = ''
      try {
        const res = await this.$axios.get(`/user/${user.id}/balance-details`)
        if (res.data.code !== 200) throw new Error(res.data.msg || '加载失败')
        this.summary = {
          balance: res.data.data.balance || 0,
          totalIncome: res.data.data.totalIncome || 0,
          totalExpense: res.data.data.totalExpense || 0,
          transactions: res.data.data.transactions || []
        }
        user.balance = this.summary.balance
        localStorage.setItem('user', JSON.stringify(user))
        window.dispatchEvent(new CustomEvent('balance-updated', { detail: this.summary.balance }))
      } catch (err) {
        this.error = err.response?.data?.msg || err.message || '余额明细加载失败'
      } finally {
        this.loading = false
      }
    },
    openRecharge() {
      window.dispatchEvent(new Event('open-recharge'))
    },
    money(value) {
      return Number(value || 0).toFixed(2)
    },
    formatTime(value) {
      if (!value) return '时间未知'
      const date = new Date(value)
      if (Number.isNaN(date.getTime())) return value
      return new Intl.DateTimeFormat('zh-CN', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit', hour12: false
      }).format(date).replace(/\//g, '-')
    },
    amountPrefix(item) {
      if (item.status !== 'success') return ''
      return Number(item.changeAmount) >= 0 ? '+' : '-'
    },
    amountClass(item) {
      if (item.status !== 'success') return 'amount-muted'
      return Number(item.changeAmount) >= 0 ? 'amount-income' : 'amount-expense'
    },
    statusText(status) {
      return { pending: '待确认', rejected: '未通过' }[status] || status
    }
  }
}
</script>

<style scoped>
.wallet-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 10px 0 48px;
  color: #27221e;
  text-align: left;
}
.page-heading { display: flex; align-items: center; justify-content: space-between; margin: 12px 2px 24px; }
.eyebrow { color: #d98738; font-size: 11px; font-weight: 800; letter-spacing: 2.4px; margin-bottom: 6px; }
.page-heading h1 { font-size: 30px; line-height: 1.2; margin: 0; color: #27221e; }
.heading-note { color: #93877d; font-size: 14px; margin-top: 7px; }
.refresh-button {
  width: auto; display: flex; align-items: center; gap: 7px; margin: 0; padding: 9px 14px;
  color: #76685e; background: #fff; border: 1px solid #eadfd4; border-radius: 12px;
}
.refresh-button svg { width: 16px; fill: none; stroke: currentColor; stroke-width: 1.8; }
.balance-card {
  position: relative; min-height: 190px; padding: 30px 34px; overflow: hidden;
  display: flex; align-items: flex-end; justify-content: space-between;
  color: #fff; border-radius: 24px;
  background: linear-gradient(135deg, #38251d 0%, #704227 54%, #bc7137 100%);
  box-shadow: 0 18px 38px rgba(91, 52, 28, .22);
}
.balance-glow { position: absolute; width: 280px; height: 280px; right: -40px; top: -125px; border-radius: 50%; background: rgba(255, 220, 163, .16); }
.balance-main, .recharge-button { position: relative; z-index: 2; }
.balance-label { display: block; color: #f0d6c2; font-size: 14px; margin-bottom: 8px; }
.balance-value { font: 700 44px/1.1 Georgia, serif; letter-spacing: .5px; }
.balance-value small { font: 500 23px/1 sans-serif; margin-right: 6px; }
.account-name { display: block; color: #e4c4ac; font-size: 12px; margin-top: 12px; }
.recharge-button {
  width: auto; margin: 0; padding: 11px 18px; color: #6d3f22; background: #fff7ed;
  border-radius: 12px; font-weight: 700; box-shadow: 0 6px 18px rgba(55, 30, 18, .2);
}
.recharge-button:hover { background: #fff; }
.recharge-button span { font-size: 18px; margin-right: 3px; }
.card-mark { position: absolute; right: 25px; top: 10px; font: 700 88px/1 serif; color: rgba(255,255,255,.065); transform: rotate(-8deg); }
.summary-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin: 18px 0; }
.summary-card { display: flex; align-items: center; gap: 14px; padding: 18px 20px; background: #fff; border: 1px solid #eee5dc; border-radius: 16px; }
.summary-icon { width: 42px; height: 42px; display: grid; place-items: center; border-radius: 12px; }
.summary-icon svg { width: 20px; fill: none; stroke: currentColor; stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round; }
.summary-card.income .summary-icon { color: #3c9872; background: #eaf7f1; }
.summary-card.expense .summary-icon { color: #c26b47; background: #fff0e9; }
.summary-card span { display: block; color: #998c82; font-size: 12px; margin-bottom: 3px; }
.summary-card strong { font-size: 18px; color: #3d352f; }
.details-card { background: #fff; border: 1px solid #eee5dc; border-radius: 20px; overflow: hidden; }
.details-header { display: flex; justify-content: space-between; align-items: center; padding: 23px 24px 18px; border-bottom: 1px solid #f2ebe5; }
.details-header h2 { text-align: left; margin: 0 0 4px; font-size: 20px; color: #302923; }
.details-header p { color: #a2968d; font-size: 12px; }
.filters { display: flex; padding: 3px; background: #f7f2ee; border-radius: 10px; }
.filters button { width: auto; margin: 0; padding: 7px 14px; color: #8f8278; background: transparent; border-radius: 8px; font-size: 12px; }
.filters button:hover { background: #f0e6de; }
.filters button.active { color: #77472b; background: #fff; box-shadow: 0 2px 8px rgba(85, 53, 32, .09); }
.transaction-item { display: flex; gap: 15px; padding: 20px 24px; border-bottom: 1px solid #f3ede8; }
.transaction-item:last-child { border-bottom: 0; }
.transaction-icon { flex: 0 0 43px; height: 43px; display: grid; place-items: center; border-radius: 13px; }
.transaction-icon svg { width: 21px; fill: none; stroke: currentColor; stroke-width: 1.8; stroke-linecap: round; stroke-linejoin: round; }
.transaction-icon.recharge { color: #3d9674; background: #eaf7f1; }
.transaction-icon.payment { color: #b96947; background: #fff0e9; }
.transaction-icon.refund { color: #517fbd; background: #edf4ff; }
.transaction-content { min-width: 0; flex: 1; }
.transaction-title-row { display: flex; justify-content: space-between; align-items: baseline; gap: 12px; }
.transaction-title-row h3 { margin: 0; color: #37302a; font-size: 15px; }
.transaction-title-row strong { white-space: nowrap; font-size: 16px; }
.amount-income { color: #368c6b; }
.amount-expense { color: #3d352f; }
.amount-muted { color: #a99f97; }
.transaction-content > p { margin: 4px 0 9px; color: #8f837a; font-size: 12px; }
.transaction-meta { display: flex; flex-wrap: wrap; gap: 6px 16px; color: #b0a69e; font-size: 11px; }
.status-tag { padding: 1px 7px; border-radius: 8px; }
.status-tag.pending { color: #b47b25; background: #fff4d8; }
.status-tag.rejected { color: #a15b55; background: #fdeceb; }
.state-box { min-height: 260px; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #9a8f87; text-align: center; }
.state-box h3 { margin: 12px 0 5px; color: #5a5049; font-size: 16px; }
.state-box p { font-size: 13px; }
.empty-icon { width: 54px; height: 54px; display: grid; place-items: center; color: #c49872; background: #f8eee5; border-radius: 50%; font: 700 22px Georgia, serif; }
.loading-ring { width: 28px; height: 28px; margin-bottom: 12px; border: 3px solid #eee4dc; border-top-color: #b76d38; border-radius: 50%; animation: spin .8s linear infinite; }
.error-state button { width: auto; padding: 8px 18px; }
@keyframes spin { to { transform: rotate(360deg); } }
@media (max-width: 680px) {
  .wallet-page { padding-top: 0; }
  .page-heading h1 { font-size: 25px; }
  .heading-note { display: none; }
  .balance-card { min-height: 180px; padding: 26px 22px; border-radius: 19px; }
  .balance-value { font-size: 37px; }
  .recharge-button { padding: 10px 13px; }
  .summary-grid { grid-template-columns: 1fr; gap: 10px; }
  .details-header { align-items: flex-start; gap: 14px; flex-direction: column; }
  .transaction-item { padding: 17px 16px; }
  .transaction-meta span:nth-child(2) { max-width: 100%; overflow: hidden; text-overflow: ellipsis; }
}
</style>
