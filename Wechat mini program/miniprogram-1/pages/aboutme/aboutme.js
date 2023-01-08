// pages/aboutme/aboutme.js
Page({

    /**
     * 页面的初始数据
     */
    data: {

    },

    objectGO1:function()
  {
    wx.navigateTo({
      url: '/pages/index/index',
    })
  },
  objectGO2:function()
  {
    wx.navigateTo({
      url: '/pages/objectIntroduce/objectIntroduce',
    })
  },
  intro:function()
  {
      wx.showToast({
        title: '敬请期待',
      })
  }
})