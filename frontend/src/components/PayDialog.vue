<template>
  <div class="pay-dialog-overlay" v-if="visible">
    <div class="pay-dialog">
      <div class="dialog-header">
        <h3>{{ payType === 'wechat' ? '微信支付' : '支付宝支付' }}</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>
      <div class="dialog-body">
        <div class="order-info">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付金额：</span>
            <span class="value price">¥{{ order.price }}</span>
          </div>
        </div>
        <div class="pay-notice">
          <p class="notice-text">⚠️ 请事先拨打电话给厨师，询问厨师该时间段是否有空，支付后无法取消订单</p>
        </div>
        <div class="qrcode-container">
          <img
                :src="qrcodeUrl"
                alt="支付二维码"
                class="qrcode"
          />
          <p class="tip">请使用{{ payType === 'wechat' ? '微信' : '支付宝' }}扫描二维码完成支付</p>
        </div>
      </div>
      <div class="dialog-footer">
        <button @click="confirmPay" class="confirm-btn">我已完成支付</button>
      </div>
    </div>
  </div>
</template>

<script>
import wechatQrcode from '/src/assets/wechat-qrcode.png'
import alipayQrcode from '/src/assets/alipay-qrcode.png'

export default {
  name: 'PayDialog',
  data() {
    return {
      wechatQrcode,
      alipayQrcode
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    payType: {
      type: String,
      default: 'wechat'
    },
    order: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    qrcodeUrl() {
      if (this.payType === 'wechat') {
        return this.wechatQrcode
      } else {
        return this.alipayQrcode
      }
    }
  },
  methods: {
    close() {
      this.$emit('close')
    },
    async confirmPay() {
      try {
        const res = await this.$axios.put(`/order/pay/${this.order.orderNo}`, {
          paymentMethod: this.payType === 'wechat' ? '微信支付' : '支付宝支付'
        })
        if (res.data.code === 200) {
          this.$emit('success')
          this.$emit('close')
        } else {
          alert('支付失败：' + res.data.msg)
        }
      } catch (err) {
        alert('支付异常：' + err.message)
      }
    }
  }
}
</script>

<style scoped>
.pay-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.pay-dialog {
  background: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.dialog-body {
  padding: 20px;
}

.order-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.value.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.qrcode-container {
  text-align: center;
}

.pay-notice {
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 4px;
  padding: 12px 15px;
  margin-bottom: 20px;
}

.notice-text {
  color: #e6a23c;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.qrcode {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  background: white;
}

.tip {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.dialog-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  text-align: center;
}

.confirm-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.confirm-btn:hover {
  background: #66b1ff;
}
</style>