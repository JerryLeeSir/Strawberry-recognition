// index.js
// 获取应用实例
const app = getApp()

Page
({
  data: {
    source:'',
    filps:'',
    openid:'xiaochengxu',
  },
  // 上传图片
  uploadimg:function()
  {
    var that = this;
    wx.chooseImage
    ({
      
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success (res) {
        that.setData({
          filps:res.tempFilePaths,
          source: res.tempFilePaths
        })
        console.log("选择成功"+that.data.filps)
        console.log(that.data.openid)


           /**
   * 生命周期函数--监听页面加载
   */
    wx.showLoading({
      title: '识别中',
    })
    wx.uploadFile
        ({
          
          filePath: that.data.filps[0],
          name: 'headImg',
          //url: 'https://aptx4869.ltd',
          url:'https://aptx4869.ltd',
          method:'POST',
          header:{
            'content-type':'application/x-www-form-urlencoded'
          },
          formData:{
              openid:that.data.openid
          },
          
          success (res)
          {
            wx.hideLoading();//隐藏加载
            /*******主要部分********/
            console.log(res.data)
            var base64Img = res.data
            var imgData = base64Img.replace(/[\r\n]/g, '')
            that.setData({
              source: 'data:image/jpg;base64,'+imgData
            })
            /*********************/
          },
          fail(res){
            console.log("失败！")
          }          
        })
      }
      
    })
  },


  formSubmit:function(e)
  {
    var that=this;
    console.log("信息："+formData)
    console.log("选择成功2"+that.data.filps)
      /**
   * 生命周期函数--监听页面加载
   */
    wx.showLoading({
      title: '加载中',
    })
    wx.uploadFile
        ({
          
          filePath: that.data.filps[0],
          name: 'headImg',
          //url: 'https://aptx4869.ltd',
          url:'https://aptx4869.ltd',
          method:'POST',
          header:{
            'content-type':'application/x-www-form-urlencoded'
          },
          formData:{
              openid:that.data.openid
          },
          
          success (res)
          {
            wx.hideLoading();//隐藏加载
            /*******主要部分********/
            console.log(res.data)
            var base64Img = res.data
            var imgData = base64Img.replace(/[\r\n]/g, '')
            that.setData({
              source: imgData
            })
            /*********************/
          },
          fail(res){
            console.log("失败！")
          }          
        })
  },
  objectGO2:function()
  {
    wx.navigateTo({
      url: '/pages/objectIntroduce/objectIntroduce',
    })
  },
  objectGO3:function()
  {
    wx.navigateTo({
      url: '/pages/aboutme/aboutme',
    })
  }
})
