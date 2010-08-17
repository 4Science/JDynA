package it.cilea.osd.jdyna.service;

import it.cilea.osd.jdyna.model.Soggettario;
import it.cilea.osd.jdyna.service.ValidatorService.ValidationResult;

public interface IValidatorSubjectService extends IValidatorDynaService {

	/** Controlla se la voce del soggetto � univoca sul soggettario padre */
	public ValidationResult controllaVoceSuSoggettario(Soggettario soggettario,String voce);
	
	/** Controlla se il nome del soggettario � univoco nel db*/
	public ValidationResult controllaNomeSuSoggettario(Soggettario soggettario,String nome);
	
	
}
