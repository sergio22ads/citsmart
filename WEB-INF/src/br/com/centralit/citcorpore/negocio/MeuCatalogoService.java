package br.com.centralit.citcorpore.negocio;

import java.util.Collection;

import br.com.centralit.citcorpore.bean.MeuCatalogoDTO;
import br.com.citframework.excecao.ServiceException;
import br.com.citframework.service.CrudServiceEjb2;

public interface MeuCatalogoService extends CrudServiceEjb2 {

	public Collection<MeuCatalogoDTO> findByCondition(Integer i) throws ServiceException, Exception;	
	
	public Collection<MeuCatalogoDTO>  findByIdServicoAndIdUsuario(Integer idServico, Integer idUsuario) throws ServiceException, Exception;	
}
