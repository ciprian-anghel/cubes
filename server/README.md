## To start application, the following configuration needs to be done:
1. In Environment variables set the following:
	- GOOGLE_APPLICATION_CREDENTIALS = <path to application credentials JSON file>  
	- GOOGLE_CLOUD_STORAGE_PROJECT_ID = <cloud storage project id>  
	- GOOGLE_CLOUD_STORAGE_BUCKET_ID = <cloud storage bucket id>  
2. A folder named "cubes" needs to be present in the bucket. It will be used as the root.

## Google Firebase Cloud Storage
- [Firebase Cloud Storage](https://cloud.google.com/storage/docs/reference/libraries#client-libraries-install-java)
- [Google Cloud Storage Client Library Java](https://cloud.google.com/storage/docs/reference/libraries)

## Create an application_default_credentials.json file.
- Open CMD
- `gcloud auth application-default login` will generate `application_default_credentials.json`
- set `GOOGLE_APPLICATION_CREDENTIALS` environment variable with the JSON's path

Documentation at: [provide-credentials-adc](https://cloud.google.com/docs/authentication/provide-credentials-adc#how-to)
