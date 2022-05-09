# -*- coding: utf-8 -*-
from sklearn.preprocessing import MinMaxScaler
from PIL import Image
import numpy as np
import pandas as pd
from tqdm import tqdm
import timm
import torch
from sklearn.metrics.pairwise import cosine_similarity
import os

def feature_extractor(path,model):
    device = 'cuda:0' if torch.cuda.is_available() else 'cpu'
    img = Image.open(path)
    img = np.transpose(img,(2,0,1))
    img = torch.tensor([img],dtype=torch.float32).cuda()
    img.to(device)
    model.to(device)
    feat = model(img).to(device)
    return feat

## Collect Data
def image_rc(data):
    device = 'cuda:0' if torch.cuda.is_available() else 'cpu'
    model = timm.create_model('efficientnet_b0',pretrained=True).to(device)
    feature_list = np.array([])
    for i in tqdm(data['img']):
        feat = feature_extractor(i,model).view(-1)
        feat = feat.cpu().detach().numpy()
        feature_list = np.append(feature_list,feat)
    feature_list = feature_list.reshape(data.shape[0],-1)
    df = pd.DataFrame(cosine_similarity(feature_list),columns = data['text'].values.tolist(),index = data['text'].values.tolist())
    return feature_list,df

# +
def inference(path,model,df,feature_list): # 여기서 df 원본 df
    img = feature_extractor(path,model).view(-1)
    img = img.cpu().detach().numpy()
    img = img.reshape(1000)
    cos_ls = []
    for i in range(len(feature_list)):
        cos_ls.append(cosine_similarity([feature_list[i]],[img])[0][0])
    df['cos'] = cos_ls
    scaler = MinMaxScaler()
    rating = scaler.fit_transform(sample['rating'].values.reshape(-1,1))
    df['score'] = rating.reshape(-1) * 0.5 + df['cos']*0.5
    
    df = df.sort_values('score',ascending=False).reset_index(drop=True)
    top5_text = df.iloc[:4]['text'].values    
    top5_style = df.iloc[:5]['style'] # 평점 + 이미지 유사도
    dic = {0:'picasso',1:'pop art',2:'monet'}
    top5_style = top5_style.map(dic)
    recommend_style = top5_style.value_counts().index[0]
    return top5_text,recommend_style

if __name__ == '__main__':
    device = 'cuda:0' if torch.cuda.is_available() else 'cpu'
    feature_list = np.load('feature_list.npy')
#     feature_list,df = image_rc(sample)
    sample = pd.read_csv('final_recommend.csv')
    model = timm.create_model('efficientnet_b0',pretrained=True).to(device)
    text,style = inference('output.png',model,sample,feature_list)
    print(text)
    print(style)
