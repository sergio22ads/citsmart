package br.com.centralit.citcorpore.integracao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.centralit.citcorpore.bean.PaisDTO;
import br.com.citframework.dto.IDto;
import br.com.citframework.integracao.CrudDaoDefaultImpl;
import br.com.citframework.integracao.Field;
import br.com.citframework.integracao.Order;
import br.com.citframework.util.Constantes;

@SuppressWarnings({"rawtypes","unchecked"})
public class PaisDao extends CrudDaoDefaultImpl {

	public PaisDao() {
		super(Constantes.getValue("DATABASE_ALIAS"), null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Collection find(IDto obj) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getFields() {
		Collection listFields = new ArrayList();
		
		listFields.add(new Field("idpais", "idPais", true, false, false, true));
		listFields.add(new Field("nomePais", "nomePais", true, false, false, true));
		
		return listFields;
	}

	@Override
	public String getTableName() {
		
		return "PAIS";
	}

	@Override
	public Collection list() throws Exception {
		
		List list = new ArrayList();
		list.add(new Order("nomePais"));
		return super.list(list);
	}

	
	@Override
	public Class getBean() {
		
		return PaisDTO.class;
		
	}

}
