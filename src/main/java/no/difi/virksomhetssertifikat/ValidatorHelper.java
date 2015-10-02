package no.difi.virksomhetssertifikat;

import no.difi.virksomhetssertifikat.api.CertificateValidationException;
import no.difi.virksomhetssertifikat.api.CertificateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Encapsulate validator for a more extensive API.
 */
public class ValidatorHelper implements CertificateValidator {

    private static Logger logger = LoggerFactory.getLogger(ValidatorHelper.class);

    private static CertificateFactory certFactory;

    static {
        try {
            certFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("Unable to load certificate factory.", e);
        }
    }

    private CertificateValidator certificateValidator;

    public ValidatorHelper(CertificateValidator certificateValidator) {
        this.certificateValidator = certificateValidator;
    }

    public void validate(X509Certificate certificate) throws CertificateValidationException {
        certificateValidator.validate(certificate);
    }

    public void validate(InputStream inputStream) throws CertificateValidationException {
        validate(getCertificate(inputStream));
    }

    public void validate(byte[] certificate) throws CertificateValidationException {
        validate(getCertificate(certificate));
    }

    public boolean isValid(X509Certificate certificate) {
        try {
            certificateValidator.validate(certificate);
            return true;
        } catch (CertificateValidationException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    public boolean isValid(InputStream inputStream) {
        try {
            return isValid(getCertificate(inputStream));
        } catch (CertificateValidationException e) {
            logger.debug(e.getMessage(), e);
            return false;
        }
    }

    public boolean isValid(byte[] certificate) {
        try {
            return isValid(getCertificate(certificate));
        } catch (CertificateValidationException e) {
            logger.debug(e.getMessage(), e);
            return false;
        }
    }

    public static X509Certificate getCertificate(byte[] cert) throws CertificateValidationException {
        return getCertificate(new ByteArrayInputStream(cert));
    }

    public static X509Certificate getCertificate(InputStream inputStream) throws CertificateValidationException {
        try {
            return (X509Certificate) certFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw new CertificateValidationException(e.getMessage(), e);
        }
    }
}
