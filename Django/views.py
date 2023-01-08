
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from django.http import HttpResponse
from django.shortcuts import render, render_to_response
from django import forms  # Django表单方式上传图片
from django.http import HttpResponse
from django.http import HttpRequest
from picture.models import User
import base64
import argparse
import os

import smtplib
from email.mime.text import MIMEText
from datetime import date


import cv2
import matplotlib.pyplot as plt
import numpy as np
import datetime
import tensorflow as tf
from picture.lib.config import config as cfg
from picture.lib.utils.nms_wrapper import nms
from picture.lib.utils.test import im_detect
# from nets.resnet_v1 import resnetv1
from picture.lib.nets.vgg16 import vgg16
from picture.lib.utils.timer import Timer

from os import listdir

class UserForm(forms.Form):
    username = forms.CharField(label="文件名")
    headImg = forms.FileField(label="文件")

# faster rcnn 中train.py中的部分代码
CLASSES = ('__background__',
           'strawberry', 'w', 'unstrawberry')
NETS = {'vgg16': ('vgg16.ckpt',), 'res101': ('res101_faster_rcnn_iter_110000.ckpt',)}
DATASETS = {'pascal_voc': ('voc_2007_trainval',), 'pascal_voc_0712': ('voc_2007_trainval',)}

def vis_detections(ax, class_name, dets, thresh, s_count):
    """Draw detected bounding boxes."""
    inds = np.where(dets[:, -1] >= thresh)[0]
    if len(inds) == 0:
        return
    '''
    im = im[:, :, (2, 1, 0)]
    fig, ax = plt.subplots(figsize=(12, 12))
    ax.imshow(im, aspect='equal')
    for i in inds:
        bbox = dets[i, :4]
        score = dets[i, -1]
    '''
    #############添加的代码
    for i in inds:
        bbox = dets[i, :4]
        score = dets[i, -1]
        ############
        if class_name == 'strawberry':
            s_count[0] = s_count[0] + 1
            ax.add_patch(
                plt.Rectangle((bbox[0], bbox[1]),
                              bbox[2] - bbox[0],
                              bbox[3] - bbox[1], fill=False,
                              edgecolor='red', linewidth=3.5)
            )
            ax.text(bbox[0], bbox[1] - 2,
                    '{:s} {:.3f}  [{}]'.format(class_name, score, s_count[0]),
                    bbox=dict(facecolor='blue', alpha=0.5),
                    fontsize=14, color='white')
            '''
            ax.set_title(('{} detections with '
                          'p({} | box) >= {:.1f}\nstrawberry have {} | unstrawberry have {}').format(class_name, class_name,
                                                          thresh, strawberry_count, unstrawberry_count),
                         fontsize=14)
            '''
        else:
            s_count[1] = s_count[1] + 1
            ax.add_patch(
                plt.Rectangle((bbox[0], bbox[1]),
                              bbox[2] - bbox[0],
                              bbox[3] - bbox[1], fill=False,
                              edgecolor='blue', linewidth=3.5)
            )
            ax.text(bbox[0], bbox[1] - 2,
                    '{:s} {:.3f}  [{}]'.format(class_name, score, s_count[1]),
                    bbox=dict(facecolor='blue', alpha=0.5),
                    fontsize=14, color='white')
            '''
            ax.set_title(('{} detections with '
                          'p({} | box) >= {:.1f}\nstrawberry have {} | unstrawberry have {}').format(class_name, class_name,
                                                          thresh, strawberry_count, unstrawberry_count),
                         fontsize=14)
            '''

    plt.axis('off')
    plt.tight_layout()
    plt.draw()

def demo(sess, net, image_name):
    """Detect object classes in an image using pre-computed object proposals."""

    # Load the demo image
    im_file = os.path.join(cfg.FLAGS2["data_dir"], 'demo', image_name)
    im = cv2.imread(im_file)

    # Detect all object classes and regress object bounds
    timer = Timer()
    timer.tic()
    scores, boxes = im_detect(sess, net, im)
    timer.toc()
    print('Detection took {:.3f}s for {:d} object proposals'.format(timer.total_time, boxes.shape[0]))

    # Visualize detections for each class
    CONF_THRESH = 0.5
    NMS_THRESH = 0.1
    # 添加的代码
    im = im[:, :, (2, 1, 0)]
    fig, ax = plt.subplots(figsize=(12, 12))
    ax.imshow(im, aspect='equal')
    plt.ion()
    ##########
    s_count = [0, 0]
    for cls_ind, cls in enumerate(CLASSES[1:]):
        cls_ind += 1  # because we skipped background
        cls_boxes = boxes[:, 4 * cls_ind:4 * (cls_ind + 1)]
        cls_scores = scores[:, cls_ind]
        dets = np.hstack((cls_boxes,
                          cls_scores[:, np.newaxis])).astype(np.float32)
        keep = nms(dets, NMS_THRESH)
        dets = dets[keep, :]
        vis_detections(ax, cls, dets, CONF_THRESH, s_count)
    ax.set_title(('strawberry and unstrawberry detections with '
                  'p(strawberry | box) >= {:.1f} p(unstrawberry | box) >= {:.1f}\nstrawberry have {} | unstrawberry have {}').format(
        CONF_THRESH,
        CONF_THRESH, s_count[0],
        s_count[1]),
                 fontsize=14)

def parse_args():
    """Parse input arguments."""
    parser = argparse.ArgumentParser(description='Tensorflow Faster R-CNN demo')
    parser.add_argument('--net', dest='demo_net', help='Network to use [vgg16 res101]',
                        choices=NETS.keys(), default='vgg16')
    parser.add_argument('--dataset', dest='dataset', help='Trained dataset [pascal_voc pascal_voc_0712]',
                        choices=DATASETS.keys(), default='pascal_voc_0712')
    args = parser.parse_args()

    return args

# 邮件发送功能，有人访问网站时，发送邮件提醒
def emailgo(text):
        msg_from = 'xxxxxxxxx@qq.com'  # 发送方邮箱
        passwd = 'xxxxxxxxxxxxxx'  # 填入发送方邮箱的授权码
        # msg_to = 'xxxxxxxxxxxxxxxxx@qq.com'  # 收件人邮箱

        # subject = "python邮件测试"  # 主题
        content = "有人访问草莓识别网站！"+text  # 正文
        msg = MIMEText(content)
        msg['Subject'] = "出现网站访问者"
        msg['From'] = msg_from
        msg['To'] = "xxxxxxxxxxxxxxxx@163.com"
        try:
            s = smtplib.SMTP_SSL("smtp.qq.com", 465)  # 邮件服务器及端口号
            s.login(msg_from, passwd)
            s.sendmail(msg_from, "xxxxxxxxxxxxxx@163.com", msg.as_string())
            print("发送成功")
        except s.SMTPException as e:
            print("发送失败")
        finally:
            s.quit()

def register(request):
    
    if request.method == "POST":
        
        imagee = request.FILES['headImg']
        print(imagee)
        openid = request.POST.get('openid')
        print(openid)
        if openid!=None:
            # 存储图片,这里需要修改为自己的地址
            basepir = '/www/wwwroot/DjangoFaster/myfaster/picture/data/demo/'
            with open(basepir+openid+'.jpg','wb') as f:
                f.write(imagee.read())
                f.close()
        
        if openid=="xiaochengxu":
            emailgo("小程序")
            # 开始识别的时刻
            timee = datetime.datetime.now()
            now_date = datetime.datetime.strftime(timee,'%Y-%m-%d-%H-%M-%S')
            print('----------------------------------------------------'+str(now_date))
            # 这里需要修改为自己的地址
            a = listdir('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo')  # 11.28 by wj
            old_file_path = os.path.join('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo',
                                         a[0])  # 11.28 by wj
            new_file_path = '/www/wwwroot/DjangoFaster/myfaster/picture/data/demo/'+str(now_date)+'.jpg'  # 11.28 by wj
            os.rename(old_file_path, new_file_path)  # 11.28 by wj   将保存下来的图片改名成上传时间.jpg
            
            ##保存上传图片到另一个文件夹，这里需要修改为自己的地址
            f_src = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo/'+str(now_date)+'.jpg','rb')
            content = f_src.read()
            
            f_copy = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo_log_oringin/'+str(now_date)+'.jpg','wb')
            f_copy.write(content)
            #f_copy.write(content[:-100000])截取图片
            #   
            f_src.close()
            f_copy.close()
            
            
            # args = parse_args()
            tf.reset_default_graph()
            # model path
            # demonet = args.demo_net
            # dataset = args.dataset
            # 这里需要修改为自己的地址
            tfmodel = '/www/wwwroot/DjangoFaster/myfaster/picture/output/vgg16/voc_2007_trainval/default/vgg16.ckpt'
            # tfmodel = '/www/wwwroot/DjangoFaster/myfaster/picture/output/vgg16/voc_2007_trainval/default'
            
            if not os.path.isfile(tfmodel + '.meta'):
                print(tfmodel)
                
                raise IOError(('{:s} not found.\nDid you download the proper networks from '
                               'our server and place them properly?').format(tfmodel + '.meta'))
                
            # set config
            tfconfig = tf.ConfigProto(allow_soft_placement=True)
            tfconfig.gpu_options.allow_growth = True

            # init session
            sess = tf.Session(config=tfconfig)
            # load network
            if 'vgg16' == 'vgg16':
                net = vgg16(batch_size=1)
            # elif demonet == 'res101':
            # net = resnetv1(batch_size=1, num_layers=101)
            else:
                raise NotImplementedError

            n_classes = len(CLASSES)
            # create the structure of the net having a certain shape (which depends on the number of classes)
            net.create_architecture(sess, "TEST", n_classes,
                                    tag='default', anchor_scales=[8, 16, 32])
            saver = tf.train.Saver()
            saver.restore(sess, tfmodel)

            print('Loaded network {:s}'.format(tfmodel))

            im_names = [str(now_date)+'.jpg']
            for im_name in im_names:
                print('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
                print('Demo for data/demo/{}'.format(im_name))
                demo(sess, net, im_name)
                plt.savefig(str("/www/wwwroot/DjangoFaster/myfaster/test/"+now_date)+'.jpg')
            os.remove(new_file_path)  # 11.28 by wj    在上传新的图片前清空upload文件夹，是每次demo处理时upload里只有一张名为000001.jpg图片
            imagepath = '/www/wwwroot/DjangoFaster/myfaster/test/'+str(now_date)+'.jpg'
            #image_data = open(imagepath, "rb").read()
            
            
            f_src_s = open('/www/wwwroot/DjangoFaster/myfaster/test/'+str(now_date)+'.jpg','rb')
            contents = f_src_s.read()
            f_copy_s = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo_logo_test/'+str(now_date)+'.jpg','wb')
            f_copy_s.write(contents)
            #f_copy.write(content[:-100000])截取图片    
            #
            f_src_s.close()
            f_copy_s.close()
            

            img_im = cv2.imread(imagepath)  # 要被base转换的图片地址
            receive_base = base64.b64encode(cv2.imencode('.jpg', img_im)[1]).decode()  # 把转换的编码赋值
            print(receive_base)  # 17292 #返回图片的base码的字母个数

            return HttpResponse(receive_base)
            #return HttpResponse(image_data, content_type="image/jpg")
            
        uf = UserForm(request.POST, request.FILES)
        if uf.is_valid():
            emailgo("APP或网站")
            # 获取表单信息
            username = uf.cleaned_data['username']
            headImg = uf.cleaned_data['headImg']
            print("uuuuuu-------"+username)
            
            # 写入数据库
            user = User()
            user.username = username
            user.headImg = headImg
            user.save()
            
            timee = datetime.datetime.now()
            now_date = datetime.datetime.strftime(timee,'%Y-%m-%d-%H-%M-%S')
            print('----------------------------------------------------'+str(now_date))
            
            a = listdir('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo')  # 11.28 by wj
            old_file_path = os.path.join('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo',
                                         a[0])  # 11.28 by wj
            new_file_path = '/www/wwwroot/DjangoFaster/myfaster/picture/data/demo/'+str(now_date)+'.jpg'  # 11.28 by wj
            os.rename(old_file_path, new_file_path)  # 11.28 by wj   将保存下来的图片改名成上传时间.jpg
            
            ##保存上传图片到另一个文件夹
            f_src = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo/'+str(now_date)+'.jpg','rb')
            content = f_src.read()
            
            f_copy = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo_log_oringin/'+str(now_date)+'.jpg','wb')
            f_copy.write(content)
            #f_copy.write(content[:-100000])截取图片
            #   
            f_src.close()
            f_copy.close()
            
            
            # args = parse_args()
            tf.reset_default_graph()
            # model path
            # demonet = args.demo_net
            # dataset = args.dataset
            tfmodel = '/www/wwwroot/DjangoFaster/myfaster/picture/output/vgg16/voc_2007_trainval/default/vgg16.ckpt'
            # tfmodel = '/www/wwwroot/DjangoFaster/myfaster/picture/output/vgg16/voc_2007_trainval/default'
            
            if not os.path.isfile(tfmodel + '.meta'):
                print(tfmodel)
                
                raise IOError(('{:s} not found.\nDid you download the proper networks from '
                               'our server and place them properly?').format(tfmodel + '.meta'))
                
            # set config
            tfconfig = tf.ConfigProto(allow_soft_placement=True)
            tfconfig.gpu_options.allow_growth = True

            # init session
            sess = tf.Session(config=tfconfig)
            # load network
            if 'vgg16' == 'vgg16':
                net = vgg16(batch_size=1)
            # elif demonet == 'res101':
            # net = resnetv1(batch_size=1, num_layers=101)
            else:
                raise NotImplementedError

            n_classes = len(CLASSES)
            # create the structure of the net having a certain shape (which depends on the number of classes)
            net.create_architecture(sess, "TEST", n_classes,
                                    tag='default', anchor_scales=[8, 16, 32])
            saver = tf.train.Saver()
            saver.restore(sess, tfmodel)

            print('Loaded network {:s}'.format(tfmodel))

            im_names = [str(now_date)+'.jpg']
            for im_name in im_names:
                print('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
                print('Demo for data/demo/{}'.format(im_name))
                demo(sess, net, im_name)
                plt.savefig(str("/www/wwwroot/DjangoFaster/myfaster/test/"+now_date)+'.jpg')
            os.remove(new_file_path)  # 11.28 by wj    在上传新的图片前清空upload文件夹，是每次demo处理时upload里只有一张名为000001.jpg图片
            imagepath = '/www/wwwroot/DjangoFaster/myfaster/test/'+str(now_date)+'.jpg'
            image_data = open(imagepath, "rb").read()
            
            
            f_src_s = open('/www/wwwroot/DjangoFaster/myfaster/test/'+str(now_date)+'.jpg','rb')
            contents = f_src_s.read()
            f_copy_s = open('/www/wwwroot/DjangoFaster/myfaster/picture/data/demo_logo_test/'+str(now_date)+'.jpg','wb')
            f_copy_s.write(contents)
            #f_copy.write(content[:-100000])截取图片    
            #
            f_src_s.close()
            f_copy_s.close()
            
            
            #return render_to_response('index.html', {'uf': image_data})
            return HttpResponse(image_data, content_type="image/jpg")
    else:
        uf = UserForm()
    return render_to_response('index.html', {'uf': uf})
