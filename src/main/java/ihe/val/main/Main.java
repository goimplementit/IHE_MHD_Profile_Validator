package ihe.val.main;

import static spark.Spark.post;

import java.util.List;
import java.util.stream.Collectors;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.DocumentManifest;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

public class Main {

	private static final String LINE_BREAK = System.getProperty("line.separator");
	
	private static final FhirContext ctx = FhirContext.forDstu2();

	public static void main(String[] args) {
		post("/validate/documentmanifest", (req, res) -> {
			System.out.println("/validate/documentmanifest "+req.ip());
			final String body = req.body();
			try {
				final Bundle bundle = parseBundle(body);
				final List<Entry> entries = bundle.getEntry();
				final List<IResource> resources = entries.stream()
											.map(Entry::getResource)
											.collect(Collectors.toList());
				final boolean allResourcesAreOfTypeDocumentManifest = resources.stream()
																			   .allMatch(r -> DocumentManifest.class.isInstance(r));
				
				if(!allResourcesAreOfTypeDocumentManifest) {
					return "Not valid! Bundle contains non DocumentManifest resources." + LINE_BREAK;
				}
				
				final List<DocumentManifest> documentManifests = resources.stream()
																		  .map(r -> (DocumentManifest)r)
																		  .collect(Collectors.toList());
				
				//Profile specific checks
				final boolean allHaveASubject = documentManifests.stream().allMatch(DocumentManifestPredicates::hasASubject);
				if(!allHaveASubject) {
					return "Not valid! Not all DocumentManifest resources have a valid subject" + LINE_BREAK;
				}
				
				final boolean ifAuthorIsPresentItIsAContainedResource = documentManifests.stream().allMatch(DocumentManifestPredicates::ifAuthorPresentItHasAContainedResource);
				if(!ifAuthorIsPresentItIsAContainedResource) {
					return "Not valid! Not all DocumentManifest authors have contained resources of type practitioner" + LINE_BREAK;
				}
			} catch(DataFormatException e) {
				return "Not valid! #### " + e.getMessage() + LINE_BREAK;
			}
			return "Valid! " + LINE_BREAK;
		});
		
		post("/validate/documentreference", (req, res) -> {
			System.out.println("/validate/documentreference "+req.ip());
			final String body = req.body();
			try {
				final Bundle bundle = parseBundle(body);
				final List<Entry> entries = bundle.getEntry();
				final List<IResource> resources = entries.stream()
											.map(Entry::getResource)
											.collect(Collectors.toList());
				final boolean allResourcesAreOfTypeDocumentReference = resources.stream()
																			   .allMatch(r -> DocumentReference.class.isInstance(r));
				
				if(!allResourcesAreOfTypeDocumentReference) {
					return "Not valid! Bundle contains non DocumentReference resources." + LINE_BREAK;
				}
				
				final List<DocumentReference> documentReferences = resources.stream()
																		  .map(r -> (DocumentReference)r)
																		  .collect(Collectors.toList());
				
				//Profile specific checks
				final boolean allHaveAMasterId = documentReferences.stream().allMatch(r -> r.getMasterIdentifier() != null);
				if(!allHaveAMasterId) {
					return "Not valid! Not all DocumentReference resources have a master identifier" + LINE_BREAK;
				}
				
				final boolean allHaveASubject = documentReferences.stream().allMatch(DocumentReferencePredicates::hasASubject);
				if(!allHaveASubject) {
					return "Not valid! Not all DocumentReference resources have a subject" + LINE_BREAK;
				}
				
				final boolean ifAuthorIsPresentItIsAContainedResource = documentReferences.stream().allMatch(DocumentReferencePredicates::ifAuthorPresentItHasAContainedResource);
				if(!ifAuthorIsPresentItIsAContainedResource) {
					return "Not valid! Not all DocumentReference authors have contained resources of type practitioner" + LINE_BREAK;
				}
				
				final boolean allDoNotHaveACustodian = documentReferences.stream().allMatch(r -> {
					boolean b = r.getCustodian().getReference().isEmpty();
					return b;
				});
				if(!allDoNotHaveACustodian) {
					return "Not valid! Some DocumentReference resources have a custodian" + LINE_BREAK;
				}
				
				final boolean allDoNotHaveACreated= documentReferences.stream().allMatch(r -> r.getCreated() == null);
				if(!allDoNotHaveACreated) {
					return "Not valid! Some DocumentReference resources have a created" + LINE_BREAK;
				}
				
				final boolean allDoNotHaveADocStatus= documentReferences.stream().allMatch(r -> r.getDocStatus().isEmpty());
				if(!allDoNotHaveADocStatus) {
					return "Not valid! Some DocumentReference resources have a doc status" + LINE_BREAK;
				}
				
				final boolean allContentAttachmentHasAContentType= documentReferences.stream().allMatch(DocumentReferencePredicates::allContentAttachmentHasAContentType);
				if(!allContentAttachmentHasAContentType) {
					return "Not valid! Some DocumentReference resources have content attachment without a content-type" + LINE_BREAK;
				}
				
				final boolean noContentAttachmentHasBinaryData= documentReferences.stream().allMatch(DocumentReferencePredicates::noContentAttachmentHasBinaryData);
				if(!noContentAttachmentHasBinaryData) {
					return "Not valid! Some DocumentReference resources have content attachment with binary data" + LINE_BREAK;
				}
				
				final boolean allContentAttachmentHasAUrl= documentReferences.stream().allMatch(DocumentReferencePredicates::allContentAttachmentHasAUrl);
				if(!allContentAttachmentHasAUrl) {
					return "Not valid! Some DocumentReference resources have content attachment without a url" + LINE_BREAK;
				}
				
				final boolean allContentAttachmentHasASize= documentReferences.stream().allMatch(DocumentReferencePredicates::allContentAttachmentHasASize);
				if(!allContentAttachmentHasASize) {
					return "Not valid! Some DocumentReference resources have content attachment without a size" + LINE_BREAK;
				}
				
				final boolean allContentAttachmentHasAHash= documentReferences.stream().allMatch(DocumentReferencePredicates::allContentAttachmentHasAHash);
				if(!allContentAttachmentHasAHash) {
					return "Not valid! Some DocumentReference resources have content attachment without a hash" + LINE_BREAK;
				}
				
				final boolean allContentMustHaveAFormat= documentReferences.stream().allMatch(DocumentReferencePredicates::allContentMustHaveAFormat);
				if(!allContentMustHaveAFormat) {
					return "Not valid! Some DocumentReference resources have content without a format" + LINE_BREAK;
				}
				
				final boolean allContextsMustHaveAContainedPatient = documentReferences.stream()
																					   .allMatch(r -> r.getContext().getSourcePatientInfo().getResource() instanceof Patient);
				if(!allContextsMustHaveAContainedPatient) {
					return "Not valid! Some DocumentReference resources have context without a contained sourcePatientInfo" + LINE_BREAK;
				}
			} catch(DataFormatException e) {
				return "Not valid! #### " + e.getMessage() + LINE_BREAK;
			}
			return "Valid! " + LINE_BREAK;
		});
	}

	private static Bundle parseBundle(String body) {
		final IParser xmlParser = ctx.newXmlParser();
		final IParser jsonParser = ctx.newJsonParser();
		
		try {
			return xmlParser.parseResource(Bundle.class, body);
		} catch(Exception e) {
			return jsonParser.parseResource(Bundle.class, body);
		}
	}

}
