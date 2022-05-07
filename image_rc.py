from PIL import Image
import numpy as np
import pandas as pd
from tqdm import tqdm
import timm
import torch
def feature_extractor(path,model):
    img = Image.open(path)
    img = np.transpose(img,(2,0,1))
    img = torch.tensor([img],dtype=torch.float32)
    feat = model(img)
    return feat
def image_rc(data):
    model = timm.create_model('efficientnet_b0',pretrained=True)
    feature_list = np.array([])
    for i in tqdm(data['img']):
        feat = feature_extractor(i,model).view(-1)
        feat = feat.detach().numpy()
        feature_list = np.append(feature_list,feat)
    feature_list = feature_list.reshape(data.shape[0],-1)
    df = pd.DataFrame(cosine_similarity(feature_list),columns = data['text'].values.tolist(),index = data['text'].values.tolist())
    return df
