package br.com.centralit.citcorpore.integracao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import br.com.centralit.citcorpore.bean.FaturaOSDTO;
import br.com.citframework.dto.IDto;
import br.com.citframework.integracao.Condition;
import br.com.citframework.integracao.CrudDaoDefaultImpl;
import br.com.citframework.integracao.Field;
import br.com.citframework.integracao.Order;
import br.com.citframework.util.Constantes;;

public class OSAtividadePeriodicaDao extends CrudDaoDefaultImpl {
	public OSAtividadePeriodicaDao() {
		super(Constantes.getValue("DATABASE_ALIAS"), null);
	}
	public Collection getFields() {
		Collection listFields = new ArrayList();
		listFields.add(new Field("idAtividadePeriodica" ,"idAtividadePeriodica", true, false, false, false));
		listFields.add(new Field("idOs" ,"idOs", true, false, false, false));
		return listFields;
	}
	public String getTableName() {
		return this.getOwner() + "OSAtividadePeriodica";
	}
	public Collection list() throws Exception {
		return null;
	}

	public Class getBean() {
		return FaturaOSDTO.class;
	}
	public Collection find(IDto arg0) throws Exception {
		return null;
	}
	public Collection findByIdAtividadePeriodica(Integer parm) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList(); 
		condicao.add(new Condition("idAtividadePeriodica", "=", parm)); 
		ordenacao.add(new Order("idOs"));
		return super.findByCondition(condicao, ordenacao);
	}
	public void deleteByIdAtividadePeriodica(Integer parm) throws Exception {
		List condicao = new ArrayList();
		condicao.add(new Condition("idAtividadePeriodica", "=", parm));
		super.deleteByCondition(condicao);
	}
	public Collection findByIdOs(Integer parm) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList(); 
		condicao.add(new Condition("idOs", "=", parm)); 
		ordenacao.add(new Order("idAtividadePeriodica"));
		return super.findByCondition(condicao, ordenacao);
	}
	public void deleteByIdOs(Integer parm) throws Exception {
		List condicao = new ArrayList();
		condicao.add(new Condition("idOs", "=", parm));
		super.deleteByCondition(condicao);
	}
}
