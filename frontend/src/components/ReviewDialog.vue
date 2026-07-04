<template>
  <div class="review-dialog" v-if="visible">
    <div class="dialog-content">
      <h3>服务评价</h3>
      <div class="form-item">
        <label>评分：</label>
        <div class="rating">
          <span
              v-for="i in 5"
              :key="i"
              :class="['star', { active: i <= rating }]"
              @click="rating = i"
          >★</span>
        </div>
      </div>
      <div class="form-item">
        <label>评价内容：</label>
        <textarea v-model="content" placeholder="请分享您的用餐体验..." rows="4"></textarea>
      </div>
      <div class="dialog-actions">
        <button @click="handleCancel" class="cancel-btn">取消</button>
        <button @click="handleSubmit" class="submit-btn" :disabled="!canSubmit">提交评价</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReviewDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    orderId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      rating: 5,
      content: ''
    }
  },
  computed: {
    canSubmit() {
      return this.rating > 0 && this.content.trim().length > 0
    }
  },
  methods: {
    handleCancel() {
      this.rating = 5
      this.content = ''
      this.$emit('close')
    },
    async handleSubmit() {
      if (!this.canSubmit) return

      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (!user.id) {
        alert('请先登录')
        return
      }

      try {
        const res = await this.$axios.post('/review/add', {
          orderId: this.orderId,
          userId: user.id,
          rating: this.rating,
          content: this.content
        })

        if (res.data.code === 200) {
          alert('评价成功！感谢您的反馈')
          this.$emit('success')
          this.handleCancel()
        } else {
          alert('评价失败：' + res.data.msg)
        }
      } catch (err) {
        alert('评价异常：' + err.message)
      }
    }
  }
}
</script>

<style scoped>
.review-dialog {
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
.dialog-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}
.dialog-content h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}
.form-item {
  margin-bottom: 15px;
}
.form-item label {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}
.rating {
  display: flex;
  gap: 5px;
}
.star {
  font-size: 28px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}
.star.active {
  color: #f56c6c;
}
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  font-size: 14px;
}
.dialog-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
.dialog-actions button {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.cancel-btn {
  background: #909399;
  color: #fff;
}
.submit-btn {
  background: #409eff;
  color: #fff;
}
.submit-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
</style>