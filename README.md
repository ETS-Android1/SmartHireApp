# Hire App
> The main objective of this project is designed to use artificial intelligence and implemented it in android mobile apps to extract the only necessary employee details such as name, email, phone numbers, age, address, skills and education from resume.
---

## Table of Contents
- [How it works?](#how-it-works)
- [API used](#api-used)
- [Sample User Interface](#sample-user-interface)

## How it works?
The artificial intelligence algorithms used are Optical Characters Optimization (OCR) to detect and extract all the information in a resume after it was captured by the phone camera and then follow by another artificial intelligence algorithm which is Named-Entity Recognition (NER) to capture the possible name-entity such as person, location, skills, education in the resume. OCR and NER will be used as an Application Programming Interface (API) to communicate with this project by importing its library. In addition, this system will also capture the profile photo attached to the resume by using mobile vision API and set its as default profile picture of the candidates in database. With the ease of use of mobile phone, employers can recruit their desired candidates instantly at anywhere by just taking out their phone, take a photo of candidate’s resume, and that’s all. The artificial intelligence will do the rest and store the qualified candidate’s credentials in database without any manual process. 

## API used
- Mobile Vision Text API
- Mobile Vision APIs (Face Detection APIs)
- Stanford CoreNLP

## Sample User Interface
#### Home Page
![home page](https://github.com/yujune/SmartHireApp/blob/master/screenshots/home.jpeg)
### Profile Page
![profile page](https://github.com/yujune/SmartHireApp/blob/master/screenshots/Profile.JPG)
### Upload Page
![upload page](https://github.com/yujune/SmartHireApp/blob/master/screenshots/upload.jpeg)
### Manual Form Page
![manual form](https://github.com/yujune/SmartHireApp/blob/master/screenshots/manual.jpeg)
### Extracted Details Page
![extracted details](https://github.com/yujune/SmartHireApp/blob/master/screenshots/Extracted.png)
