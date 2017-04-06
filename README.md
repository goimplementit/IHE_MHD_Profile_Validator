# IHE_MHD_Profile_Validator
Basic HL7 FHIR validator respecting parts of the IHE MHD profile. For more information about the profile visit: http://ihe.net/uploadedFiles/Documents/ITI/IHE_ITI_Suppl_MHD.pdf

Currently it can validate two types of responses:
* A bundle containing DocumentManifest resources
* A bundle containing DocumentReference resources

# Usage
You can start the service by running the main method. 

When the service is running you can POST bundles (xml or json) to the following endpoints:
* localhost:4567/validate/documentmanifest
* localhost:4567/validate/documentreference

# Example using curl
```bash
curl -XPOST --data "$content" localhost:4567/validate/documentmanifest
curl -XPOST --data "$content" localhost:4567/validate/documentreference
```

At the courtesy of GoImplement.it. Visit us on https://goimplementit.github.io/
