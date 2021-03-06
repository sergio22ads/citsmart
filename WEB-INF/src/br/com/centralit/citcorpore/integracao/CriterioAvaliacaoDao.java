package br.com.centralit.citcorpore.integracao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.centralit.citcorpore.bean.CriterioAvaliacaoDTO;
import br.com.citframework.dto.IDto;
import br.com.citframework.integracao.Condition;
import br.com.citframework.integracao.CrudDaoDefaultImpl;
import br.com.citframework.integracao.Field;
import br.com.citframework.integracao.Order;
import br.com.citframework.util.Constantes;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class CriterioAvaliacaoDao extends CrudDaoDefaultImpl {
	public CriterioAvaliacaoDao() {
		super(Constantes.getValue("DATABASE_ALIAS"), null);
	}

	public Collection getFields() {
		Collection listFields = new ArrayList();
		listFields.add(new Field("idCriterio", "idCriterio", true, true, false, false));
		listFields.add(new Field("descricao", "descricao", false, false, false, false));
		listFields.add(new Field("aplicavelCotacao", "aplicavelCotacao", false, false, false, false));
		listFields.add(new Field("aplicavelAvaliacaoSolicitante", "aplicavelAvaliacaoSolicitante", false, false, false, false));
		listFields.add(new Field("aplicavelAvaliacaoComprador", "aplicavelAvaliacaoComprador", false, false, false, false));
		listFields.add(new Field("aplicavelQualificacaoFornecedor", "aplicavelQualificacaoFornecedor", false, false, false, false));
		listFields.add(new Field("tipoAvaliacao", "tipoAvaliacao", false, false, false, false));
		return listFields;
	}

	public String getTableName() {
		return this.getOwner() + "CriterioAvaliacao";
	}

	public Collection list() throws Exception {
		return null;
	}

	public Class getBean() {
		return CriterioAvaliacaoDTO.class;
	}

	public Collection find(IDto arg0) throws Exception {
		return null;
	}

	public Collection findByAplicavelCotacao() throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("aplicavelCotacao", "=", "S"));
		ordenacao.add(new Order("descricao"));
		return super.findByCondition(condicao, ordenacao);
	}

	public Collection findByAplicavelAvaliacaoSolicitante() throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("aplicavelAvaliacaoSolicitante", "=", "S"));
		ordenacao.add(new Order("descricao"));
		return super.findByCondition(condicao, ordenacao);
	}

	public Collection findByAplicavelAvaliacaoComprador() throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("aplicavelAvaliacaoComprador", "=", "S"));
		ordenacao.add(new Order("descricao"));
		return super.findByCondition(condicao, ordenacao);
	}

	/**
	 * Retorna true ou false em rela��o ao criterio basado
	 * 
	 * @param criterioAvaliacaoDto
	 * @return boolean
	 * @throws Exception
	 * @author thays.araujo
	 */
	public boolean verificarSeCriterioExiste(CriterioAvaliacaoDTO criterioAvaliacaoDto) throws Exception {
		List parametro = new ArrayList();
		List list = new ArrayList();
		String sql = "select idCriterio From " + getTableName() + "  where  descricao = ?  ";

		if (criterioAvaliacaoDto.getIdCriterio() != null) {
			sql += " and idCriterio <> " + criterioAvaliacaoDto.getIdCriterio();
		}

		parametro.add(criterioAvaliacaoDto.getDescricao());
		list = this.execSQL(sql, parametro.toArray());
		if (list != null && !list.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
