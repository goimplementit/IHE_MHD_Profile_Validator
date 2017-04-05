package ihe.val.main;

import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;

public class DocumentReferencePredicates {
	private static final String PATIENT = "Patient";
	
	public static boolean hasASubject(DocumentReference m) {
		final ResourceReferenceDt subject = m.getSubject();
		final boolean resourceTypePatient = subject.getResource() instanceof Patient;
		return subject != null && resourceTypePatient;
	}
	
	public static boolean ifAuthorPresentItHasAContainedResource(DocumentReference m) {
		if(m.getAuthor().isEmpty()) {
			return true;
		}
		
		final Set<IBaseResource> authorsOfTypePractitioner = m.getAuthor().stream()
															   .map(a -> a.getResource())
															   .filter(r -> r instanceof Practitioner)
															   .collect(Collectors.toSet());
		
		return !authorsOfTypePractitioner.isEmpty();
	}
	
	public static boolean allContentAttachmentHasAContentType(DocumentReference m) {
		return m.getContent().stream()
							 .filter(c -> c.getAttachment() != null)
							 .allMatch(c -> c.getAttachment().getContentType() != null);
	}
	
	public static boolean noContentAttachmentHasBinaryData(DocumentReference m) {
		return m.getContent().stream()
							 .filter(c -> c.getAttachment() != null)
							 .allMatch(c -> c.getAttachment().getData() == null);
	}
	
	public static boolean allContentAttachmentHasAUrl(DocumentReference m) {
		return m.getContent().stream()
							 .filter(c -> c.getAttachment() != null)
							 .allMatch(c -> c.getAttachment().getUrl() != null);
	}
	
	public static boolean allContentAttachmentHasASize(DocumentReference m) {
		return m.getContent().stream()
							 .filter(c -> c.getAttachment() != null)
							 .allMatch(c -> c.getAttachment().getSize() != null);
	}
	
	public static boolean allContentAttachmentHasAHash(DocumentReference m) {
		return m.getContent().stream()
							 .filter(c -> c.getAttachment() != null)
							 .allMatch(c -> c.getAttachment().getHash() != null);
	}
	
	public static boolean allContentMustHaveAFormat(DocumentReference m) {
		return m.getContent().stream()
							 .allMatch(c -> c.getFormat() != null && c.getFormat().size() == 1);
	}
}
