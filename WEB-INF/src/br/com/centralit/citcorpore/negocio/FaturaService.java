package br.com.centralit.citcorpore.negocio;
import java.sql.Date;
import java.util.Collection;

import br.com.centralit.citcorpore.bean.FaturaDTO;
import br.com.citframework.service.CrudServicePojo;
public interface FaturaService extends CrudServicePojo {
	public Collection findByIdContrato(Integer parm) throws Exception;
	public void deleteByIdContrato(Integer parm) throws Exception;
	public Collection findByIdContratoAndPeriodoAndSituacao(Integer idContrato, Date dataInicio, Date dataFim, String[] situacao) throws Exception;
	public void updateSituacao(Integer idFatura, String situacao, String aprovacaoGestor, String aprovacaoFiscal) throws Exception;
}
