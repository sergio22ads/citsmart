package br.com.centralit.citcorpore.integracao;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.centralit.citcorpore.bean.ServicoDTO;
import br.com.centralit.citcorpore.util.CITCorporeUtil;
import br.com.citframework.dto.IDto;
import br.com.citframework.integracao.Condition;
import br.com.citframework.integracao.CrudDaoDefaultImpl;
import br.com.citframework.integracao.Field;
import br.com.citframework.integracao.Order;
import br.com.citframework.util.Constantes;
import br.com.citframework.util.SQLConfig;
import br.com.citframework.util.UtilDatas;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class ServicoDao extends CrudDaoDefaultImpl {
	public ServicoDao() {
		super(Constantes.getValue("DATABASE_ALIAS"), null);
	}

	public Collection getFields() {
		Collection listFields = new ArrayList();
		listFields.add(new Field("idServico", "idServico", true, true, false, false));
		listFields.add(new Field("idCategoriaServico", "idCategoriaServico", false, false, false, false));
		listFields.add(new Field("idSituacaoServico", "idSituacaoServico", false, false, false, false));
		listFields.add(new Field("idTipoServico", "idTipoServico", false, false, false, false));
		listFields.add(new Field("idImportanciaNegocio", "idImportanciaNegocio", false, false, false, false));
		listFields.add(new Field("idEmpresa", "idEmpresa", false, false, false, false));
		listFields.add(new Field("idTipoEventoServico", "idTipoEventoServico", false, false, false, false));
		listFields.add(new Field("idTipoDemandaServico", "idTipoDemandaServico", false, false, false, false));
		listFields.add(new Field("idLocalExecucaoServico", "idLocalExecucaoServico", false, false, false, false));
		listFields.add(new Field("nomeServico", "nomeServico", false, false, false, false));
		listFields.add(new Field("detalheServico", "detalheServico", false, false, false, false));
		listFields.add(new Field("objetivo", "objetivo", false, false, false, false));
		listFields.add(new Field("passosServico", "passosServico", false, false, false, false));
		listFields.add(new Field("dataInicio", "dataInicio", false, false, false, false));
		listFields.add(new Field("linkProcesso", "linkProcesso", false, false, false, false));
		listFields.add(new Field("descricaoProcesso", "descricaoProcesso", false, false, false, false));
		listFields.add(new Field("tipoDescProcess", "tipoDescProcess", false, false, false, false));
		listFields.add(new Field("dispPortal", "dispPortal", false, false, false, false));
		listFields.add(new Field("siglaAbrev", "siglaAbrev", false, false, false, false));
		// listFields.add(new Field("quadroOrientPortal" ,"quadroOrientPortal", false, false, false, false));
		listFields.add(new Field("deleted", "deleted", false, false, false, false));
		listFields.add(new Field("idBaseconhecimento", "idBaseconhecimento", false, false, false, false));
		listFields.add(new Field("idTemplateSolicitacao", "idTemplateSolicitacao", false, false, false, false));
		listFields.add(new Field("idTemplateAcompanhamento", "idTemplateAcompanhamento", false, false, false, false));
		return listFields;
	}

	private Timestamp transformaHoraFinal(Date data) throws ParseException {
		String dataHora = data + " 23:59:59";
		String pattern = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		java.util.Date d = sdf.parse(dataHora);
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(d.getTime());
		return sqlDate;
	}
	/**
	 * Executando em: Oracle, sqlServer, mySql e POstgreSQL
	 **/
	public Collection<ServicoDTO> listaQuantidadeServicoAnalitico(ServicoDTO servicoDTO) throws Exception {
		List listRetorno = new ArrayList();
		List parametro = new ArrayList();
		List listaQuantidadeServicoAnalitico = new ArrayList<ServicoDTO>();

		/* Desenvolvedor: Rodrigo Pecci - Data: 31/10/2013 - Hor�rio: 15h35min - ID Citsmart: 120770
		 * Motivo/Coment�rio: Novas cl�usulas foram adicionadas no where para garantir a consist�ncia do relat�rio. 
		 */
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT solicitacaoservico.idsolicitacaoservico, ");
		sql.append("       tipocomplexidade.desctipocomplexidade, ");
		sql.append("       servico.nomeservico, ");
		sql.append("       tipodemandaservico.nometipodemandaservico, ");
		sql.append("       atividadesservicocontrato.custoatividade, ");
		sql.append("       usuario.nome, ");
		sql.append("       CASE ");
		sql.append("         WHEN solicitacaoservico.datahorafim > solicitacaoservico.datahoralimite THEN 'nao' ");
		sql.append("         ELSE 'sim' ");
		sql.append("       END slaatendida, ");
		sql.append("       solicitacaoservico.tempoatendimentohh, ");
		sql.append("       solicitacaoservico.tempoatendimentomm, "); 
		sql.append("       solicitacaoservico.situacao "); 
		sql.append("FROM   solicitacaoservico solicitacaoservico ");
		sql.append("       INNER JOIN bpm_itemtrabalhofluxo bpm_itemtrabalhofluxo ");
		sql.append("               ON solicitacaoservico.idtarefaencerramento = bpm_itemtrabalhofluxo.iditemtrabalho ");
		sql.append("       INNER JOIN tipodemandaservico tipodemandaservico ");
		sql.append("               ON tipodemandaservico.idtipodemandaservico = solicitacaoservico.idtipodemandaservico ");
		sql.append("       INNER JOIN servicocontrato servicocontrato ");
		sql.append("               ON servicocontrato.idservicocontrato = solicitacaoservico.idservicocontrato ");
		sql.append("       INNER JOIN contratos ");
		sql.append("               ON servicocontrato.idcontrato = contratos.idcontrato ");
		sql.append("              AND (upper(contratos.deleted)  = 'N' or contratos.deleted is null) ");
		sql.append("       INNER JOIN servico servico ");
		sql.append("               ON servico.idservico = servicocontrato.idservico ");
		sql.append("       LEFT JOIN atividadesservicocontrato atividadesservicocontrato ");
		sql.append("              ON atividadesservicocontrato.idservicocontratocontabil = servicocontrato.idservicocontrato ");
		sql.append("                 AND atividadesservicocontrato.contabilizar = 'S' ");
		sql.append("       LEFT JOIN tipocomplexidade tipocomplexidade ");
		sql.append("              ON tipocomplexidade.complexidade = atividadesservicocontrato.complexidade ");
		sql.append("       LEFT JOIN usuario usuario ");
		sql.append("              ON usuario.idusuario = bpm_itemtrabalhofluxo.idresponsavelatual ");
		
		if (CITCorporeUtil.SGBD_PRINCIPAL.toUpperCase().equals(SQLConfig.ORACLE)) {
			sql.append("where (to_char(bpm_itemtrabalhofluxo.datahorafinalizacao, 'YYYY-MM-DD') BETWEEN ? AND ? ) ");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			parametro.add(formatter.format(servicoDTO.getDataInicio()));
			parametro.add(formatter.format(servicoDTO.getDataFim()));
		} else {
			sql.append("WHERE  bpm_itemtrabalhofluxo.datahorafinalizacao BETWEEN ? AND ? ");
			parametro.add(servicoDTO.getDataInicio());
			parametro.add(transformaHoraFinal(servicoDTO.getDataFim()));
		}
		if (servicoDTO.getIdContrato() != null) {
			sql.append("AND servicocontrato.idcontrato = ? ");
			parametro.add(servicoDTO.getIdContrato());
		}
		if (servicoDTO.getIdTipoDemandaServico() != null) {
			sql.append("AND tipodemandaservico.idtipodemandaservico = ? ");
			parametro.add(servicoDTO.getIdTipoDemandaServico());
		}
		
		sql.append(" AND (upper(contratos.deleted) = 'N' or contratos.deleted is null) ");
		sql.append(" AND solicitacaoservico.idtipodemandaservico is not null ");
		
		sql.append("GROUP BY solicitacaoservico.idsolicitacaoservico ");
		sql.append("        ,tipocomplexidade.desctipocomplexidade ");
		sql.append("        ,servico.nomeservico ");
		sql.append("        ,tipodemandaservico.nometipodemandaservico ");
		sql.append("        ,usuario.nome ");
		sql.append("        ,atividadesservicocontrato.custoatividade ");
		sql.append("        ,solicitacaoservico.datahorafim ");
		sql.append("        ,solicitacaoservico.datahoralimite ");
		sql.append("        ,solicitacaoservico.tempoatendimentohh ");
		sql.append("        ,solicitacaoservico.tempoatendimentomm "); 
		sql.append("        ,solicitacaoservico.situacao "); 

		
		sql.append("ORDER BY solicitacaoservico.idsolicitacaoservico ");
		sql.append("        ,tipocomplexidade.desctipocomplexidade ");
		sql.append("        ,servico.nomeservico ");
		sql.append("        ,tipodemandaservico.nometipodemandaservico ");
		sql.append("        ,usuario.nome ");
		sql.append("        ,atividadesservicocontrato.custoatividade ");
		sql.append("        ,solicitacaoservico.datahorafim ");
		sql.append("        ,solicitacaoservico.datahoralimite ");
		sql.append("        ,solicitacaoservico.tempoatendimentohh ");
		sql.append("        ,solicitacaoservico.tempoatendimentomm "); 
		sql.append("        ,solicitacaoservico.situacao "); 
		
		List lista = new ArrayList();
		lista = this.execSQL(sql.toString(), parametro.toArray());
		listRetorno.add("idSolicitacao");
		listRetorno.add("complexidade");
		listRetorno.add("nomeServico");
		listRetorno.add("nomeTipoDemandaServico");
		listRetorno.add("custoAtividade");
		listRetorno.add("nomeSolucionador");
		listRetorno.add("slaAtendido");
		listRetorno.add("tempoAtendimentoHH");
		listRetorno.add("tempoAtendimentoMM");
		listRetorno.add("situacao");
		
		if (lista != null && !lista.isEmpty()) {
			listaQuantidadeServicoAnalitico = this.engine.listConvertion(ServicoDTO.class, lista, listRetorno);
			return listaQuantidadeServicoAnalitico;
		}
		return Collections.emptyList();
	}

	public String getTableName() {
		return this.getOwner() + "Servico";
	}

	public Collection list() throws Exception {
		List ordenacao = new ArrayList();
		ordenacao.add(new Order("idServico"));
		return super.list(ordenacao);
	}

	public Class getBean() {
		return ServicoDTO.class;
	}

	public Collection find(IDto arg0) throws Exception {
		List ordenacao = new ArrayList();
		ordenacao.add(new Order("idServico"));
		return super.find(arg0, ordenacao);
	}

	public Collection findByIdCategoriaServico(Integer parm) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("idCategoriaServico", "=", parm));
		ordenacao.add(new Order("idServicoContrato"));
		return super.findByCondition(condicao, ordenacao);
	}

	public void deleteByIdCategoriaServico(Integer parm) throws Exception {
		List condicao = new ArrayList();
		condicao.add(new Condition("idCategoriaServico", "=", parm));
		super.deleteByCondition(condicao);
	}

	public Collection findByIdSituacaoServico(Integer parm) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("idSituacaoServico", "=", parm));
		condicao.add(new Condition("deleted", "<>", "Y"));
		condicao.add(new Condition(Condition.OR, "deleted", "is", null));
		ordenacao.add(new Order("idServico"));
		return super.findByCondition(condicao, ordenacao);
	}

	public void deleteByIdSituacaoServico(Integer parm) throws Exception {
		List condicao = new ArrayList();
		condicao.add(new Condition("idSituacaoServico", "=", parm));
		super.deleteByCondition(condicao);
	}

	public Collection findByIdTipoDemandaAndIdCategoria(Integer idTipoDemanda, Integer idCategoria) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("idTipoDemandaServico", "=", idTipoDemanda));
		if (idCategoria != null)
			condicao.add(new Condition("idCategoriaServico", "=", idCategoria));
		ordenacao.add(new Order("nomeServico"));
		return super.findByCondition(condicao, ordenacao);
	}
	/**
	 * Executando em: Oracle, sqlServer, mySql e POstgreSQL
	 **/
	public Collection findByIdTipoDemandaAndIdContrato(Integer idTipoDemanda, Integer idContrato, Integer idCategoria) throws Exception {
		Collection fields = getFields();
		List listaNomes = new ArrayList();
		
		Date dataAtual = UtilDatas.getDataAtual();
		Object[] objs = new Object[] {dataAtual};
		
		String sql = "SELECT ";
		int i = 0;
		for (Iterator it = fields.iterator(); it.hasNext();) {
			Field field = (Field) it.next();
			if (i > 0) {
				sql += ",";
			}
			
			field.setFieldDB("servico." + field.getFieldDB());
			
			sql += field.getFieldDB();
			listaNomes.add(field.getFieldClass());
			i++;
		}
		sql += " FROM " + this.getTableName();
		sql += " INNER JOIN situacaoservico on situacaoservico.idsituacaoservico = servico.idsituacaoservico ";
		sql += " WHERE idTipoDemandaServico = " + idTipoDemanda + " AND ";
		sql += " situacaoservico.nomesituacaoservico = 'Ativo' AND ";
		if (idCategoria != null) {
			sql += " idCategoriaServico = " + idCategoria + " AND ";
		}
		sql += " idServico IN (SELECT idservico FROM servicocontrato WHERE idcontrato = " + idContrato + " AND (datafim is null OR datafim > ?)) AND";

		if (CITCorporeUtil.SGBD_PRINCIPAL.toUpperCase().equals(SQLConfig.POSTGRESQL))
			sql += "(UPPER(deleted) IS NULL OR UPPER(deleted) = 'N') ";
		else
			sql += "(deleted IS NULL OR deleted = 'N' OR deleted = 'n') ";

		sql += " ORDER BY nomeServico";

		List listaR = this.execSQL(sql, objs);
		return this.listConvertion(ServicoDTO.class, listaR, listaNomes);
	}
	
	public Collection findByNomeAndContratoAndTipoDemandaAndCategoria(Integer idTipoDemanda, Integer idContrato, Integer idCategoria, String nome) throws Exception {

		List fields = new ArrayList();
		Date dataAtual = UtilDatas.getDataAtual();
		if(nome == null)
			nome = "";
		
		String text = nome;
		//text = Normalizer.normalize(text, Normalizer.Form.NFD);
		//text = text.replaceAll("[^\\p{ASCII}]", "");
		//text = text.replaceAll("���������������������������`^''-+=", "aaaaeeiooouucAAAAEEIOOOUUCc ");
		text = text.replaceAll("�`^''-+=", "");
		nome = text;		
		nome = "%"+nome+"%";
		Object[] objs = new Object[] {dataAtual,nome}; 
		
		String sql = "SELECT idServico, nomeServico";
		sql += " FROM servico "; 
		sql += " INNER JOIN situacaoservico on situacaoservico.idsituacaoservico = servico.idsituacaoservico ";
		sql += " WHERE idTipoDemandaServico = " + idTipoDemanda + " AND ";
		sql += " situacaoservico.nomesituacaoservico = 'Ativo' AND ";
		if (idCategoria != null) {
			sql += " idCategoriaServico = " + idCategoria + " AND ";
		}
		sql += " idServico IN (SELECT idservico FROM servicocontrato WHERE idcontrato = " + idContrato + " AND (datafim is null OR datafim > ?)) AND";

		if (CITCorporeUtil.SGBD_PRINCIPAL.toUpperCase().equals(SQLConfig.POSTGRESQL))
			sql += "(UPPER(deleted) IS NULL OR UPPER(deleted) = 'N') AND nomeServico ilike ? ";
		else
			sql += "(deleted IS NULL OR deleted = 'N' OR deleted = 'n') AND nomeServico like ? ";

		sql += " ORDER BY nomeServico";

		List listaR = this.execSQL(sql, objs);
		
		fields.add("idServico");
		fields.add("nomeServico");
		
		
		return this.listConvertion(ServicoDTO.class, listaR, fields);
	}

	/**
	 * Retorna sigla do servico pelo idOs.
	 * 
	 * @param idOs
	 * @return
	 * @throws Exception
	 */
	public String retornaSiglaPorIdOs(Integer idOs) throws Exception {
		List lstParametros = new ArrayList();
		String sql = "SELECT servico.siglaabrev FROM servicocontrato";
		sql = sql + " INNER JOIN servico ON servico.idservico = servicocontrato.idservico";
		sql = sql + " INNER JOIN os ON os.idservicocontrato = servicocontrato.idservicocontrato";
		sql = sql + " WHERE os.idos = ? ";
		lstParametros.add(idOs);

		Object[] parametros = lstParametros.toArray();

		List lstDados = super.execSQL(sql, parametros);

		List fields = new ArrayList();
		fields.add("siglaAbrev");

		Collection<ServicoDTO> listDeFaturas = super.listConvertion(ServicoDTO.class, lstDados, fields);

		if (listDeFaturas != null && !listDeFaturas.isEmpty()) {
			for (ServicoDTO servico : listDeFaturas) {
				if (servico.getSiglaAbrev() != null && !servico.getSiglaAbrev().trim().equals("")) {
					return servico.getSiglaAbrev();
				} else {
					return "           -";
				}
			}
			return null;
		} else {
			return null;
		}

	}

	/**
	 * Retorna lista Servi�o por nome.
	 * 
	 * @return Collection
	 * @throws Exception
	 */
	public Collection findByNome(ServicoDTO servicoDTO) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();

		condicao.add(new Condition("nomeServico", "=", servicoDTO.getNomeServico()));
		ordenacao.add(new Order("nomeServico"));
		return super.findByCondition(condicao, ordenacao);
	}

	/**
	 * M�todo para retornar apenas os servi�os referente a unidade do solicitante
	 * 
	 * @author rodrigo.oliveira
	 * @param idServico
	 * @param idTipoDemanda
	 * @param idCategoria
	 * @return
	 * @throws Exception
	 */
	public Collection findByIdServicoAndIdTipoDemandaAndIdCategoria(Integer idServico, Integer idTipoDemanda, Integer idCategoria) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("idTipoDemandaServico", "=", idTipoDemanda));
		if (idCategoria != null)
			condicao.add(new Condition("idCategoriaServico", "=", idCategoria));
		condicao.add(new Condition("idServico", "=", idServico));
		ordenacao.add(new Order("nomeServico"));
		return super.findByCondition(condicao, ordenacao);
	}

	public ServicoDTO findByIdServico(Integer idServico) throws Exception {

		List parametro = new ArrayList();
		List fields = new ArrayList();
		List list = new ArrayList();
		String sql = "   select nometiposervico, nomeservico, nomecategoriaservico, idServico from servico " + "inner join tiposervico  on servico.idtiposervico = tiposervico.idtiposervico "
				+ "inner join categoriaservico on servico.idcategoriaservico = categoriaservico.idcategoriaservico "
				+ "where servico.deleted is null and situacao = 'A' and servico.idServico = ? order by nomecategoriaservico";
		parametro.add(idServico);
		list = this.execSQL(sql, parametro.toArray());
		fields.add("nomeTipoServico");
		fields.add("nomeServico");
		fields.add("nomeCategoriaServico");
		fields.add("idServico");
		if (list != null && !list.isEmpty()) {
			return (ServicoDTO) this.listConvertion(getBean(), list, fields).get(0);
		} else {
			return null;
		}
	}

	public Collection<ServicoDTO> findByServico(Integer idServico) throws Exception {
		List parametro = new ArrayList();
		List fields = new ArrayList();
		List list = new ArrayList();
		String sql = "   select nometiposervico, nomeservico, nomecategoriaservico, idServico from servico " + "inner join tiposervico  on servico.idtiposervico = tiposervico.idtiposervico "
				+ "inner join categoriaservico on servico.idcategoriaservico = categoriaservico.idcategoriaservico "
				+ "where servico.deleted is null and situacao = 'A' and servico.idServico = ? and (dispportal = 'Y' or dispportal = 'S') order by nomecategoriaservico";
		parametro.add(idServico);
		list = this.execSQL(sql, parametro.toArray());
		fields.add("nomeTipoServico");
		fields.add("nomeServico");
		fields.add("nomeCategoriaServico");
		fields.add("idServico");
		if (list != null && !list.isEmpty()) {
			return (Collection<ServicoDTO>) this.listConvertion(getBean(), list, fields);
		} else {
			return null;
		}
	}
		
	public Collection<ServicoDTO> findByServico(Integer idServico, String nome) throws Exception {
		List parametro = new ArrayList();
		List fields = new ArrayList();
		List list = new ArrayList();
		String sql = "   select nometiposervico, nomeservico, nomecategoriaservico, idServico from servico " + "inner join tiposervico  on servico.idtiposervico = tiposervico.idtiposervico "
				+ "inner join categoriaservico on servico.idcategoriaservico = categoriaservico.idcategoriaservico "
				+ "where servico.deleted is null and situacao = 'A' and servico.idServico = ? and (dispportal = 'Y' or dispportal = 'S') and servico.nomeservico like '%" + nome
				+ "%' order by nomecategoriaservico";
		parametro.add(idServico);
		list = this.execSQL(sql, parametro.toArray());
		fields.add("nomeTipoServico");
		fields.add("nomeServico");
		fields.add("nomeCategoriaServico");
		fields.add("idServico");
		if (list != null && !list.isEmpty()) {
			return (Collection<ServicoDTO>) this.listConvertion(getBean(), list, fields);
		} else {
			return null;
		}

	}
	public Collection listAtivos() throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("deleted", "IS", null));		
		ordenacao.add(new Order("nomeServico"));
		return super.findByCondition(condicao, ordenacao);
	}	
	
	public Collection<ServicoDTO> findByIdTemplate(Integer idTemplate) throws Exception {
		Collection<ServicoDTO> resultado = new ArrayList<ServicoDTO>();
		if (idTemplate != null) {
			List condicao = new ArrayList();
			List ordenacao = new ArrayList();

			condicao.add(new Condition("idTemplateSolicitacao", " = ", idTemplate));
			condicao.add(new Condition(Condition.OR, "idTemplateAcompanhamento", " = ", idTemplate));
			resultado = super.findByCondition(condicao, ordenacao);
		}
		return resultado;
	}
	
	public ServicoDTO findById(Integer parm) throws Exception {
		List condicao = new ArrayList();
		List ordenacao = new ArrayList();
		condicao.add(new Condition("idServico", "=", parm));
		ArrayList<ServicoDTO> lista = (ArrayList<ServicoDTO>) super.findByCondition(condicao, ordenacao);
		return (((lista==null)||(lista.size()<=0))?new ServicoDTO():lista.get(0));
	}
	
}