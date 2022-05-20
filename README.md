# Artistic Sentence

<p align='center'>
  Incheon National University - Computer Science Capstone Design(Graduation product)
</p>

<p align='center'>
  <b>You & AI</b>
</p>

<p align='center'> We make an application that <b>generates picture</b> through text. In addition, <b>not only one sentence but also several sentences</b> are summarized to create a picture that fits the atmosphere of the text. Finally, the created picture is converted into the <b>style of the artist you want</b>, and <b>recommends</b> according to the user</p>

<p align='center'>
  <img src='https://github.com/winston1214/Artistic-Sentence/blob/master/picture/full_architecture.PNG?raw=true'></img>
</p>

## Member (Team : You & AI)

<table>
  <tr>
      <td align="center"><a href="https://github.com/winston1214"><img src="https://avatars.githubusercontent.com/u/47775179?v=4" width="100" height="100"><br /><sub><b>김영민</b></sub></td>
      <td align="center"><a href="https://github.com/kkyh1125"><img src="https://avatars.githubusercontent.com/u/90811540?v=4" width="100" height="100"><br /><sub><b>김영훈</b></sub></td>
      <td align="center"><a href="https://github.com/Caution-Sun"><img src="https://avatars.githubusercontent.com/u/60997821?v=4" width="100" height="100"><br /><sub><b>유의선</b></sub></td>
      <td align="center"><a href="https://github.com/wootak96"><img src="https://avatars.githubusercontent.com/u/68039225?v=4" width="100" height="100"><br /><sub><b>신우탁</b></sub></td>
     </tr>
</table>

## Enviornment

- OS : Ubuntu 20.04-server
- GPU : A4000 x 2

## How to do (AI)?

### Text2Image

**1. Enviornment setting**
```
git clone https://https://github.com/winston1214/Text2Drawing-Service
cd Artistic-Sentence/AI
git clone https://github.com/openai/CLIP # CLIP
git clone https://github.com/CompVis/taming-transformers # VQGAN
```

```
pip install torch==1.9.0+cu111 torchvision==0.10.0+cu111 torchaudio==0.9.0 -f https://download.pytorch.org/whl/torch_stable.html
pip install torchvision==0.10
pip install ftfy regex tqdm omegaconf pytorch-lightning IPython kornia imageio imageio-ffmpeg einops torch_optimizer
```

**2. Download pretrained model**
```
mkdir checkpoints

curl -L -o checkpoints/vqgan_imagenet_f16_16384.yaml -C - 'https://heibox.uni-heidelberg.de/d/a7530b09fed84f80a887/files/?p=%2Fconfigs%2Fmodel.yaml&dl=1' #ImageNet 16384
curl -L -o checkpoints/vqgan_imagenet_f16_16384.ckpt -C - 'https://heibox.uni-heidelberg.de/d/a7530b09fed84f80a887/files/?p=%2Fckpts%2Flast.ckpt&dl=1' #ImageNet 16384
```

- if you use other model, you check ```pretrained_model.sh```

**3. Inference**
```
python generate.py -p "A painting of an apple in a fruit bowl"
```


## Demo

<p align='center'>
  <img src='https://github.com/winston1214/Artistic-Sentence/blob/master/picture/UI.PNG?raw=true'></img>
</p>

https://user-images.githubusercontent.com/47775179/169488748-b0dee451-8fc7-46f7-8fd8-253fa2e56bf9.mp4

## Etc Matarials

- **Poster** : https://github.com/winston1214/Artistic-Sentence/blob/master/picture/Poster.jpg
- **Full Presentation** : https://www.youtube.com/watch?v=TBCMjT09w0Y
- **Presentation Slide** : https://www.slideshare.net/ssuserd555d6/artistic-sentence

## Reference
```
https://github.com/nerdyrodent/VQGAN-CLIP
```
