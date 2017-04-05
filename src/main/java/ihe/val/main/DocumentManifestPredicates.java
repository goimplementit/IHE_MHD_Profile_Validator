package ihe.val.main;

import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentManifest;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;

public class DocumentManifestPredicates {
	private static final String PATIENT = "Patient";

	public static boolean hasAnIdentifier(DocumentManifest m) {
		return m.getIdentifier() != null && m.getIdentifier().size() == 1;
	}
	
	public static boolean hasASubject(DocumentManifest m) {
		final ResourceReferenceDt subject = m.getSubject();
		final String resourceType = subject.getReference().getResourceType();
		return subject != null && (PATIENT.equals(resourceType) || subject.getResource() instanceof Patient);
	}
	
	public static boolean ifAuthorPresentItHasAContainedResource(DocumentManifest m) {
		if(m.getAuthor().isEmpty()) {
			return true;
		}
		
		final Set<IBaseResource> authorsOfTypePractitioner = m.getAuthor().stream()
															   .map(a -> a.getResource())
															   .filter(r -> r instanceof Practitioner)
															   .collect(Collectors.toSet());
		
		return !authorsOfTypePractitioner.isEmpty();
	}
}
