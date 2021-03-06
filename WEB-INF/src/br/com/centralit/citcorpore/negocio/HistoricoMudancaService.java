/**
 * 
 */
package br.com.centralit.citcorpore.negocio;

import java.util.List;

import br.com.centralit.citcorpore.bean.HistoricoMudancaDTO;
import br.com.centralit.citcorpore.bean.RequisicaoMudancaDTO;
import br.com.citframework.dto.IDto;
import br.com.citframework.service.CrudServiceEjb2;

public interface HistoricoMudancaService extends CrudServiceEjb2 {
	
	public List<HistoricoMudancaDTO> listHistoricoMudancaByIdRequisicaoMudanca(Integer idRequisicaoMudanca) throws Exception;
	public void updateNotNull(IDto obj) throws Exception ;
	public HistoricoMudancaDTO maxIdHistorico(RequisicaoMudancaDTO requisicaoMudancaDTO) throws Exception;
	
}