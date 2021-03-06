package br.com.centralit.citcorpore.negocio;

import java.util.Collection;

import br.com.centralit.citcorpore.bean.NotificacaoUsuarioDTO;
import br.com.centralit.citcorpore.integracao.NotificacaoUsuarioDao;
import br.com.citframework.excecao.ServiceException;
import br.com.citframework.integracao.CrudDAO;
import br.com.citframework.service.CrudServicePojoImpl;

@SuppressWarnings("serial")
public class NotificacaoUsuarioServiceEjb extends CrudServicePojoImpl implements NotificacaoUsuarioService {

	@Override
	protected CrudDAO getDao() throws ServiceException {
		// TODO Auto-generated method stub
		return new NotificacaoUsuarioDao();
	}

	@Override
	protected void validaCreate(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaUpdate(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaDelete(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void validaFind(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<NotificacaoUsuarioDTO> listaIdUsuario(Integer idNotificacao) throws Exception {
		NotificacaoUsuarioDao dao = new NotificacaoUsuarioDao();
		return dao.listaIdUsuario(idNotificacao);
	}

}
