# How to deploy the app on Google Cloud services

Steps to create project in google cloud:
A. Create project
	1. https://console.cloud.google.com/
	2. Create project

B. Create firebase project
	1. https://console.cloud.google.com/firebase > select project
	2. Cloud Storage for Firebase > Follow steps to create firease project
	3. Build > Storage > Get Started > Start in production mode > europe-west6 location
	4. create "cubes" directory > add all images and folder structure from "cube_assets/assets/cubes" within github projecrt
	5. Rules 
	```
	service firebase.storage {
	  match /b/{bucket}/o {
		match /{allPaths=**} {
		  allow read;
		}
	  }
	}
	```

C. Service Account
	1. IAM & Admin > Service Accounts
	2. There should be already 2 service accounts, one for project and another for firebase admin 
	3. select Actions on firebase service account > Manage Keys > Add key > Create new key > JSON

D. Create Secret
	1. Security > Secret Manager > Enable
	2. Create Secret > upload key json > create secret
 
D. Create Cloud Run server
	1. Cloud Run
	2. Deploy Container > Create Service
	3. Continuously deploy from a repository > Setup with cloud build > Docker: /server/Dockerfile
	4. Configure > Region > europe-west6 (!!! SAME AS Firebase)
	5. Container(s), Volumes, Networking, Security
	> - Volumes > Add Volume > Secret > select the secret from Secret Manager
	> - Container > Volume Mounts > Mount Volume > select volume with the secret > choose a path like "cubes_secret"
	> - remember the mount path. In my case: /cubes_secret/cube-secret
	> - Container > Variables & Secret > Add variables:
	> a. GOOGLE_CLOUD_STORAGE_BUCKET_ID = <bucketid from firebase>
	> b. GOOGLE_APPLICATION_CREDENTIALS = <mount path, /cubes_secret/cube-secret>
	6. Create
	Test server with https://<url>/all-options

E. Create Cloud Run client
	1. Cloud Run
	2. Deploy Container > Create Service
	3. Continuously deploy from a repository > Setup with cloud build > Docker: /client/Dockerfile
	4. Configure > Region > europe-west6 (!!! SAME AS Firebase)
	5. Create

F. Update with new URLs in project.
	1. Client > `\cubes\client\src\environments\environment.prod.ts` > update the `serverInstanceUrl`
	2. Server > `\cubes\server\src\main\java\com\cubes\config\SecurityConfig.java` > update the `allowedOrigins`