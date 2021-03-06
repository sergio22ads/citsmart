package br.com.centralit.citcorpore.metainfo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import br.com.centralit.citajax.reflexao.CitAjaxReflexao;
import br.com.centralit.citcorpore.bean.MatrizVisaoDTO;
import br.com.centralit.citcorpore.bean.ValorRecuperadoMatrizDTO;
import br.com.centralit.citcorpore.integracao.MatrizVisaoDao;
import br.com.centralit.citcorpore.metainfo.bean.CamposObjetoNegocioDTO;
import br.com.centralit.citcorpore.metainfo.bean.GrupoVisaoCamposNegocioDTO;
import br.com.centralit.citcorpore.metainfo.bean.GrupoVisaoCamposNegocioLigacaoDTO;
import br.com.centralit.citcorpore.metainfo.bean.GrupoVisaoDTO;
import br.com.centralit.citcorpore.metainfo.bean.LookupDTO;
import br.com.centralit.citcorpore.metainfo.bean.ObjetoNegocioDTO;
import br.com.centralit.citcorpore.metainfo.bean.ReturnLookupDTO;
import br.com.centralit.citcorpore.metainfo.bean.ValorVisaoCamposNegocioDTO;
import br.com.centralit.citcorpore.metainfo.bean.VisaoDTO;
import br.com.centralit.citcorpore.metainfo.bean.VisaoRelacionadaDTO;
import br.com.centralit.citcorpore.metainfo.integracao.CamposObjetoNegocioDao;
import br.com.centralit.citcorpore.metainfo.negocio.CamposObjetoNegocioService;
import br.com.centralit.citcorpore.metainfo.negocio.DinamicViewsServiceEjb;
import br.com.centralit.citcorpore.metainfo.negocio.GrupoVisaoCamposNegocioLigacaoService;
import br.com.centralit.citcorpore.metainfo.negocio.LookupService;
import br.com.centralit.citcorpore.metainfo.negocio.ObjetoNegocioService;
import br.com.centralit.citcorpore.negocio.MatrizVisaoService;
import br.com.centralit.citcorpore.util.CITCorporeUtil;
import br.com.centralit.citcorpore.util.UtilI18N;
import br.com.citframework.excecao.ServiceException;
import br.com.citframework.service.ServiceLocator;
import br.com.citframework.util.Constantes;
import br.com.citframework.util.Reflexao;
import br.com.citframework.util.UtilDatas;
import br.com.citframework.util.UtilStrings;
import br.com.citframework.util.UtilTratamentoArquivos;

@SuppressWarnings({ "rawtypes", "unused", "unchecked", "static-access" })
public class MetaUtil {
	public static String TEXT = "TEXT";
	public static String TEXTAREA = "TEXTAREA";
	public static String RADIO = "RADIO";
	public static String SELECT = "SELECT";
	public static String DATE = "DATE";
	public static String HIDDEN = "HIDDEN";
	public static String RELATION = "RELATION";
	public static String DECIMAL = "DECIMAL";
	public static String NUMBER = "NUMBER";
	public static String HTML = "HTML";
	public static String CLASS_AND_METHOD = "CLASS";

	public static String PREFIXO_TABELA_PESQUISA = "TABLE_SEARCH_";

	public static String renderViewEdit(VisaoDTO visaoDto, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws ServiceException, Exception {
		String strArrayNamesColumns = "";
		if (visaoDto.getColGrupos() != null) {
			for (Iterator it = visaoDto.getColGrupos().iterator(); it.hasNext();) {
				GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it.next();
				// out.println("<h2 class='section'> " + grupoVisaoDTO.getDescricaoGrupoVisao() + "</h2>");
				if (grupoVisaoDTO.getColCamposVisao() != null) {
					out.println("<table>");
					for (Iterator it2 = grupoVisaoDTO.getColCamposVisao().iterator(); it2.hasNext();) {
						GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it2.next();

						if (grupoVisaoCamposNegocioDTO.getVisivel() == null) {
							grupoVisaoCamposNegocioDTO.setVisivel("S");
						}

						if (!grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(MetaUtil.HIDDEN) && !grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(MetaUtil.HTML)
								&& grupoVisaoCamposNegocioDTO.getVisivel().equalsIgnoreCase("S")) {
							out.println("<tr>");
							out.print("<td style='vertical-align: middle;'>");
							out.print("	" + UtilI18N.internacionalizaString(grupoVisaoCamposNegocioDTO.getDescricaoNegocio(), request) + "");
							if (grupoVisaoCamposNegocioDTO.getObrigatorio() != null && grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
								out.print("<span style='color:red'>*</span>");
							}
							out.println("</td>");
							out.println("<td>");
							try {
								String strAux = MetaUtil.geraCampoHTML(grupoVisaoCamposNegocioDTO, request);
								out.println("	" + UtilI18N.internacionalizaString(strAux, request));
							} catch (Exception e) {
								e.printStackTrace();
							}
							out.println("</td>");
							out.println("</tr>");

							if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(DATE)) {
								out.println("<script>");
								out.println("$(function(){");
								out.println("$( \"#" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "\" ).datepicker();");
								out.println("});");
								out.println("</script>");
							}

							if (!strArrayNamesColumns.equalsIgnoreCase("")) {
								strArrayNamesColumns += ",";
							}
							if (grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto() != null) {
								strArrayNamesColumns += "\"" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "\"";
							}
						} else {
							if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(MetaUtil.HIDDEN)) {
								String campoHTML = MetaUtil.geraCampoHTML(grupoVisaoCamposNegocioDTO, request);
								out.println(UtilI18N.internacionalizaString(campoHTML, request));

								if (!strArrayNamesColumns.equalsIgnoreCase("")) {
									strArrayNamesColumns += ",";
								}
								strArrayNamesColumns += "\"" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "\"";
							}
							if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(MetaUtil.HTML)) {
								String htmlCode = grupoVisaoCamposNegocioDTO.getHtmlCode();
								String htmlCodeFinal = interpretaHTMLCode(htmlCode, out, request, response);
								out.println(htmlCodeFinal);
							}
						}
					}
					out.println("</table>");
				}
			}
		}
		return "Array(" + strArrayNamesColumns + ")"; // Array em Javascript
	}

	public static String renderViewMatriz(VisaoDTO visaoDto, JspWriter out, HttpServletRequest request, HttpServletResponse response, boolean vinculado) throws ServiceException, Exception {
		String strArrayNamesColumns = "";
		MatrizVisaoService matrizVisaoService = (MatrizVisaoService) ServiceLocator.getInstance().getService(MatrizVisaoService.class, null);
		ObjetoNegocioService objetoNegocioService = (ObjetoNegocioService) ServiceLocator.getInstance().getService(ObjetoNegocioService.class, null);
		Collection colMatriz = matrizVisaoService.findByIdVisao(visaoDto.getIdVisao());
		String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/tableSearchVinc/tableSearchVinc.load?idVisao=" + visaoDto.getIdVisao()
				+ "&load=true&matriz=true";
		if (colMatriz != null && colMatriz.size() > 0) {
			DinamicViewsServiceEjb dinamicViewEjb = new DinamicViewsServiceEjb();
			Collection colCamposPKPrincipal = new ArrayList();
			Collection colCamposTodosPrincipal = new ArrayList();
			dinamicViewEjb.setInfoSave(visaoDto.getIdVisao(), colCamposPKPrincipal, colCamposTodosPrincipal);
			String tablePrincipal = dinamicViewEjb.generateFrom(colCamposTodosPrincipal);
			MatrizVisaoDTO matrizVisaoDTO = (MatrizVisaoDTO) colMatriz.iterator().next();
			ObjetoNegocioDTO objetoNegocioDTO = new ObjetoNegocioDTO();
			objetoNegocioDTO.setIdObjetoNegocio(matrizVisaoDTO.getIdObjetoNegocio());
			objetoNegocioDTO = (ObjetoNegocioDTO) objetoNegocioService.restore(objetoNegocioDTO);
			MatrizVisaoDao matrizVisaoDao = new MatrizVisaoDao();
			ValorRecuperadoMatrizDTO valorRecuperadoMatrizDTO = matrizVisaoDao.recuperaDadosMatriz(matrizVisaoDTO);
			out.println("<table id='" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao()
					+ "' class='easyui-datagrid' data-options=\"fitColumns:true,singleSelect:true,url:'" + url + "'\">");
			if (valorRecuperadoMatrizDTO.getColDados() != null) {
				out.println("<thead><tr>");
				for (Iterator it = valorRecuperadoMatrizDTO.getColDados().iterator(); it.hasNext();) {
					Object[] obj = (Object[]) it.next();
					for (int i = 0; i < obj.length; i++) {
						String valorApres = "";
						if (i == 0) {
							valorApres = matrizVisaoDTO.getDescricaoCampo1();
						}
						if (i == 1) {
							valorApres = matrizVisaoDTO.getDescricaoCampo2();
						}
						if (i == 2) {
							valorApres = matrizVisaoDTO.getDescricaoCampo3();
						}
						out.print("<th data-options=\"field:'fld_" + i + "'\">" + UtilI18N.internacionalizaString(valorApres, request) + "</th>");
					}
					if (visaoDto.getColGrupos() != null) {
						for (Iterator it2 = visaoDto.getColGrupos().iterator(); it2.hasNext();) {
							GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it2.next();
							if (grupoVisaoDTO.getColCamposVisao() != null) {
								for (Iterator it3 = grupoVisaoDTO.getColCamposVisao().iterator(); it3.hasNext();) {
									GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it3.next();
									if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(HIDDEN)) {
										continue;
									}
									if (grupoVisaoCamposNegocioDTO.getVisivel() == null) {
										grupoVisaoCamposNegocioDTO.setVisivel("S");
									}
									String tam = "0";
									if (grupoVisaoCamposNegocioDTO.getTamanho() == null || grupoVisaoCamposNegocioDTO.getTamanho().intValue() == 0) {
										tam = "1";
									} else {
										Integer tamAux = 0;
										try {
											tamAux = new Integer(grupoVisaoCamposNegocioDTO.getTamanho());
										} catch (Exception e) {
										}
										tam = "" + (tamAux * 1);
									}
									int tamAux = Integer.parseInt(tam);
									if (tamAux < 40) {
										tamAux = 40;
									}
									tam = "" + (tamAux * (1.5));

									String options = MetaUtil.geraCampoGRID(grupoVisaoCamposNegocioDTO, request);
									options = UtilStrings.nullToVazio(options);
									if (!options.trim().equalsIgnoreCase("")) {
										options = "," + options;
									}

									String str = "<th data-options=\"field:'" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "',width:" + tam + "" + options + "\">"
											+ UtilI18N.internacionalizaString(grupoVisaoCamposNegocioDTO.getDescricaoNegocio(), request) + "</th>";
									out.print(str);
								}
							}
						}
					}
					break;
				}
				out.println("</tr></thead>");
				if (!vinculado) {
					out.println("<tbody>");
					for (Iterator it = valorRecuperadoMatrizDTO.getColDados().iterator(); it.hasNext();) {
						Object[] obj = (Object[]) it.next();
						out.println("<tr>");
						for (int i = 0; i < obj.length; i++) {
							out.print("<td>");
							out.print(obj[i].toString());
							out.print("</td>");
						}
						if (visaoDto.getColGrupos() != null) {
							for (Iterator it2 = visaoDto.getColGrupos().iterator(); it2.hasNext();) {
								GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it2.next();
								if (grupoVisaoDTO.getColCamposVisao() != null) {
									for (Iterator it3 = grupoVisaoDTO.getColCamposVisao().iterator(); it3.hasNext();) {
										GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it3.next();
										if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(HIDDEN)) {
											continue;
										}
										String valorApres = "";
										if (valorRecuperadoMatrizDTO.getCamposObjetoNegocioApres1DTO() != null) {
											if (grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB()
													.equalsIgnoreCase(valorRecuperadoMatrizDTO.getCamposObjetoNegocioApres1DTO().getNomeDB())) {
												valorApres = obj[1].toString();
											}
										}
										if (valorRecuperadoMatrizDTO.getCamposObjetoNegocioApres2DTO() != null) {
											if (grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB()
													.equalsIgnoreCase(valorRecuperadoMatrizDTO.getCamposObjetoNegocioApres2DTO().getNomeDB())) {
												valorApres = obj[2].toString();
											}
										}
										if (valorApres.trim().equalsIgnoreCase("")) {
											// Se ainda estiver vazio, eh que ainda nao conseguiu pegar o valor atual
											valorApres = recuperaValorAtualCampo(colCamposPKPrincipal, grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto(), tablePrincipal, obj, matrizVisaoDTO,
													null);
										}
										out.print("<td>");
										out.print(valorApres);
										out.print("</td>");
									}
								}
							}
						}
						out.println("</tr>");
					}
					out.println("</tbody>");
				}
			}
			out.println("</table>");
		}
		out.println("<script>");
		String str = UtilTratamentoArquivos.getStringTextFromFileTxt(CITCorporeUtil.caminho_real_app + "/WEB-INF/classes/modelo_dataGrid_MATRIZ.properties");
		str = UtilI18N.internacionalizaString(str, request);
		if (!vinculado) {
			str = str.replaceAll("\\#URL\\#", "");
		} else {
			str = str.replaceAll("\\#URL\\#", "url:'" + url + "',");
		}
		str = str.replaceAll("#tt", "#" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao());
		out.println(str);
		out.println("</script>");
		return "";
	}

	public static String recuperaValorAtualCampo(Collection colCamposPKPrincipal, CamposObjetoNegocioDTO camposObjetoNegocioDTO, String tablePrincipal, Object[] objValues,
			MatrizVisaoDTO matrizVisaoDTO, HashMap map) throws Exception {
		String sql = "SELECT " + camposObjetoNegocioDTO.getNomeDB() + " FROM " + tablePrincipal;
		CamposObjetoNegocioDTO camposObjetoNegocioChaveMatriz = new CamposObjetoNegocioDTO();
		List lstParms = new ArrayList();
		CamposObjetoNegocioDao camposObjetoNegocioDao = new CamposObjetoNegocioDao();
		camposObjetoNegocioChaveMatriz.setIdCamposObjetoNegocio(matrizVisaoDTO.getIdCamposObjetoNegocio1());
		camposObjetoNegocioChaveMatriz.setIdObjetoNegocio(matrizVisaoDTO.getIdObjetoNegocio());
		camposObjetoNegocioChaveMatriz = (CamposObjetoNegocioDTO) camposObjetoNegocioDao.restore(camposObjetoNegocioChaveMatriz);

		CamposObjetoNegocioDTO camposObjetoNegocioAux = null;
		if(colCamposPKPrincipal!= null){
			camposObjetoNegocioAux = (CamposObjetoNegocioDTO) (colCamposPKPrincipal.iterator().next());
		}
		sql = sql + " WHERE 1 = 1 ";
		if (camposObjetoNegocioChaveMatriz != null) {
			sql = sql + " AND " + camposObjetoNegocioChaveMatriz.getNomeDB() + " = ? ";
			lstParms.add(objValues[0]);
		}
		if (map != null) {
			if (colCamposPKPrincipal != null && colCamposPKPrincipal.size() > 0) {
				for (Iterator itVinc = colCamposPKPrincipal.iterator(); itVinc.hasNext();) {
					camposObjetoNegocioAux = (CamposObjetoNegocioDTO) itVinc.next();
					if (!camposObjetoNegocioAux.getNomeDB().trim().equalsIgnoreCase(camposObjetoNegocioChaveMatriz.getNomeDB().trim())) {
						sql = sql + " AND " + camposObjetoNegocioAux.getNomeDB() + " = ? ";
						Object val = map.get(camposObjetoNegocioAux.getNomeDB());
						if (val != null && camposObjetoNegocioAux.getTipoDB() != null) {
							lstParms.add(MetaUtil.convertType(camposObjetoNegocioAux.getTipoDB(), val.toString()));
						} else {
							lstParms.add(val);
						}
					}
				}
			}
		}
		MatrizVisaoDao matrizVisaoDao = new MatrizVisaoDao();
		return matrizVisaoDao.recuperaValor(sql, lstParms.toArray());
	}

	public static String interpretaHTMLCode(String htmlCode, JspWriter out, HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		htmlCode = htmlCode + "\n";
		String[] htmlCodeAux = htmlCode.split("\n");
		String htmlCodeFinal = "";
		for (int i = 0; i < htmlCodeAux.length; i++) {
			String html = htmlCodeAux[i];
			if (html == null)
				html = "";
			html = html.trim();
			if (html.startsWith("loadHTML=")) {
				String fileHtml = html.substring(9);
				InputStream in = MetaUtil.class.getResourceAsStream(fileHtml);
				String str = convertStreamToString(in);
				htmlCodeFinal = htmlCodeFinal + interpretaHTMLCode(str, out, request, response);
				;
			} else if (html.startsWith("executeClass=")) {
				String fileExecute = html.substring(13);
				if (fileExecute == null) {
					fileExecute = "";
				}
				fileExecute = fileExecute.trim();
				Class classe = MetaUtil.class.forName(fileExecute);
				Object objeto = classe.newInstance();

				Method mtd = CitAjaxReflexao.findMethod("execute", objeto);
				Object parmReals[] = new Object[3];

				parmReals[0] = out;
				parmReals[1] = request;
				parmReals[2] = response;

				Object retorno = mtd.invoke(objeto, parmReals);
				if (retorno == null) {
					retorno = new String(" ");
				}
				htmlCodeFinal = htmlCodeFinal + interpretaHTMLCode(retorno.toString(), out, request, response);
			} else {
				htmlCodeFinal = htmlCodeFinal + html;
			}
		}
		htmlCodeFinal = UtilI18N.internacionalizaString(htmlCodeFinal, request);
		return htmlCodeFinal;
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the Reader return -1 which means there's no more data to read. We use the StringWriter
		 * class to produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static void renderViewTableSearch(VisaoDTO visaoDto, JspWriter out, HttpServletRequest request) throws IOException {
		String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/tableSearch/tableSearch.load?idVisao=" + visaoDto.getIdVisao();
		out.println("<form name='form_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + "' method='POST'>");
		out.println("<table><tr><td>" + UtilI18N.internacionaliza(request, "dinamicview.pesquisa") + " </td><td><input name='termo_pesq_TABLESEARCH_" + visaoDto.getIdVisao()
				+ "' id='termo_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + "' type='text' size='30'/></td><td><button name='btn_REFRESH_VIEW' type='button' onclick='func_REFRESH_VIEW_"
				+ visaoDto.getIdVisao() + "()'>" + UtilI18N.internacionaliza(request, "dinamicview.atualizar") + "</button></td></tr></table>");
		out.println("<table id='dg_" + visaoDto.getIdVisao() + "' width='100%' cellpadding='0' cellspacing='0' class='easyui-datagrid' url='" + url
				+ "' rownumbers='true' pagination='true' singleSelect='true'>");
		out.println("<thead>");
		int i = 0;
		if (visaoDto.getColGrupos() != null) {
			for (Iterator it = visaoDto.getColGrupos().iterator(); it.hasNext();) {
				GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it.next();
				if (grupoVisaoDTO.getColCamposVisao() != null) {
					for (Iterator it2 = grupoVisaoDTO.getColCamposVisao().iterator(); it2.hasNext();) {
						Integer tam = 5;
						GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it2.next();
						tam = grupoVisaoCamposNegocioDTO.getTamanho();
						if (tam == null) {
							tam = 5;
						}
						tam = tam * 10;
						out.println("		<th field='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' width='" + tam + "'" + " sortable='true'>"
								+ UtilI18N.internacionalizaString(grupoVisaoCamposNegocioDTO.getDescricaoNegocio(), request) + "</th>");
						i++;
					}
				}
			}
		}
		out.println("</thead>");
		out.println("</table>");

		out.println("<script>");
		out.println("$(function(){");
		out.println("	$('#dg_" + visaoDto.getIdVisao() + "').datagrid({");
		out.println("	 queryParams: {sSearch: document.form_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + ".termo_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + ".value},");
		out.println("	 onBeforeLoad:function(param){");
		out.println("		param = {sSearch: document.form_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + ".termo_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + ".value};");
		out.println("	 },");
		out.println("	 onClickRow:function(rowIndex){");
		out.println("	 	var obj = $('#dg_" + visaoDto.getIdVisao() + "').datagrid('getSelected');");
		out.println("		try{ ");
		out.println("			TABLE_SEARCH_CLICK(" + visaoDto.getIdVisao() + ", '" + visaoDto.getAcaoVisaoFilhaPesqRelacionada() + "', obj, '" + url + "');");
		out.println("		}catch(e){}");
		out.println("	 }");
		out.println("	});");
		out.println("});");
		out.println("function func_REFRESH_VIEW_" + visaoDto.getIdVisao() + "(){");
		out.println("	$('#dg_" + visaoDto.getIdVisao() + "').datagrid('load', {sSearch: document.form_pesq_TABLESEARCH_" + visaoDto.getIdVisao() + ".termo_pesq_TABLESEARCH_" + visaoDto.getIdVisao()
				+ ".value});");
		out.println("}");
		out.println("</script>");
		out.println("</form>");
	}

	public static String renderExternaClass(VisaoDTO visaoDto, JspWriter out) throws IOException {
		String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/load/load.extern?className=" + visaoDto.getClasseName();
		return url;
	}

	public static void renderViewTableEditVinc_Easy(VisaoDTO visaoDto, JspWriter out, VisaoRelacionadaDTO visaoRelacionadaDTO, HttpServletRequest request, HttpServletResponse response)
			throws ServiceException, Exception {
		StringBuffer strBuff = new StringBuffer();
		String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/tableSearchVinc/tableSearchVinc.load?idVisao=" + visaoDto.getIdVisao()
				+ "&load=true&idVisaoRelacionada=" + visaoRelacionadaDTO.getIdVisaoRelacionada();
		strBuff.append("<table id=\"" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao()
				+ "\" style=\"width:auto;height:auto\" data-options=\"iconCls:'icon-edit',singleSelect:true,url:'" + url + "'\" title=\""
				+ UtilI18N.internacionalizaString("$dinamicview.editar - " + visaoRelacionadaDTO.getTitulo(), request) + "\">");
		strBuff.append("<thead>");
		strBuff.append("<tr>");
		String str = "<th data-options=\"field:'removed',width:60\">" + UtilI18N.internacionaliza(request, "citcorpore.comum.deletar") + "?</th>";
		strBuff.append(str);
		String strAreasIniciais = "";
		if (visaoDto.getColGrupos() != null) {
			for (Iterator it = visaoDto.getColGrupos().iterator(); it.hasNext();) {
				GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it.next();
				if (grupoVisaoDTO.getColCamposVisao() != null) {
					for (Iterator it2 = grupoVisaoDTO.getColCamposVisao().iterator(); it2.hasNext();) {
						GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it2.next();
						String tam = "0";
						if (grupoVisaoCamposNegocioDTO.getTamanho() == null || grupoVisaoCamposNegocioDTO.getTamanho().intValue() == 0) {
							tam = "1";
						} else {
							Integer tamAux = 0;
							try {
								tamAux = new Integer(grupoVisaoCamposNegocioDTO.getTamanho());
							} catch (Exception e) {
							}
							tam = "" + (tamAux * 1);
						}
						int tamAux = Integer.parseInt(tam);
						if (tamAux < 50) {
							tamAux = 50;
						}
						tam = "" + (tamAux * 2);

						if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(SELECT) || grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RADIO)) {
							boolean prim = true;
							strAreasIniciais += "var array_" + grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio() + " = [";
							for (Iterator itAux1 = grupoVisaoCamposNegocioDTO.getColValores().iterator(); itAux1.hasNext();) {
								ValorVisaoCamposNegocioDTO valorVisaoCamposNegocioDTO = (ValorVisaoCamposNegocioDTO) itAux1.next();
								if (!prim) {
									strAreasIniciais += ",";
								}
								String label = valorVisaoCamposNegocioDTO.getDescricao();
								label = UtilStrings.nullToVazio(label).replaceAll("'", "");
								strAreasIniciais += "{id:'" + valorVisaoCamposNegocioDTO.getValor() + "',desc:'" + label + "'}";
								prim = false;
							}
							strAreasIniciais = strAreasIniciais + "];";
						}
						if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RELATION)) {
							if (grupoVisaoCamposNegocioDTO.getTipoLigacao() == null) {
								grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
							}
							if (grupoVisaoCamposNegocioDTO.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioDTO.RELATION_NONE)) {
								grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
							}
							LookupService lookupService = (LookupService) ServiceLocator.getInstance().getService(LookupService.class, null);
							LookupDTO lookupDto = new LookupDTO();
							lookupDto.setTermoPesquisa("");
							lookupDto.setIdGrupoVisao(grupoVisaoCamposNegocioDTO.getIdGrupoVisao());
							lookupDto.setIdCamposObjetoNegocio(grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio());

							strAreasIniciais += "var array_" + grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio() + " = [";
							Collection col = lookupService.findSimple(lookupDto);
							if (col != null) {
								boolean prim = true;
								for (Iterator itX = col.iterator(); itX.hasNext();) {
									ReturnLookupDTO returnLookupDTO = (ReturnLookupDTO) itX.next();
									if (!prim) {
										strAreasIniciais += ",";
									}
									String label = returnLookupDTO.getLabel();
									label = UtilStrings.nullToVazio(label).replaceAll("'", "");
									strAreasIniciais += "{id:'" + label + "[" + returnLookupDTO.getValue() + "]',desc:'" + label + "'}";
									prim = false;
								}
							}
							strAreasIniciais = strAreasIniciais + "];";
						}
						String options = MetaUtil.geraCampoGRID(grupoVisaoCamposNegocioDTO, request);
						options = UtilStrings.nullToVazio(options);
						if (!options.trim().equalsIgnoreCase("")) {
							options = "," + options;
						}

						if (!grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(HIDDEN)) {
							str = "<th data-options=\"field:'" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "',width:" + tam + "" + options + "\">"
									+ UtilI18N.internacionalizaString(grupoVisaoCamposNegocioDTO.getDescricaoNegocio(), request) + "</th>";
							strBuff.append(str);
						}
					}
				}
			}
		}
		strBuff.append("</tr>");
		strBuff.append("</thead>");
		strBuff.append("</table>");

		out.println("<script>");
		out.println(UtilI18N.internacionalizaString(strAreasIniciais, request));
		out.println("\n\n");
		out.println("</script>");
		out.println(UtilI18N.internacionalizaString(strBuff.toString(), request));
		out.println("<script>");
		str = UtilTratamentoArquivos.getStringTextFromFileTxt(CITCorporeUtil.caminho_real_app + "/WEB-INF/classes/modelo_dataGrid_DYNAMICVIEW.properties");
		str = UtilI18N.internacionalizaString(str, request);
		str = str.replaceAll("#tt", "#" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao());
		out.println(str);
		out.println("</script>");
	}

	public static void renderViewTableEditVinc(VisaoDTO visaoDto, JspWriter out, VisaoRelacionadaDTO visaoRelacionadaDTO, HttpServletRequest request, HttpServletResponse response)
			throws ServiceException, Exception {
		out.println("<form name='formTableVinc" + visaoDto.getIdVisao() + "' id='formTableVinc" + visaoDto.getIdVisao() + "' action='" + Constantes.getValue("SERVER_ADDRESS")
				+ Constantes.getValue("CONTEXTO_APLICACAO") + "/pages/dinamicViews/dinamicViews'>");
		out.println("<img src='" + Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO")
				+ "/imagens/plus.png' border='0' style='cursor:pointer' onclick='ADICIONA_TABLE_SEARCH_" + visaoDto.getIdVisao() + "()'/>");
		out.println("<table width='100%' style='width:100%' cellpadding='0' cellspacing='0' border='0' class='display' id='" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA
				+ visaoDto.getIdVisao() + "'>");
		out.println("<thead>");
		out.println("	<tr>");
		out.println("		<th width='0%'>codes</th>");
		out.println("		<th width='1%'>&nbsp;</th>");
		int i = 0;

		String tams = "null, null";
		if (visaoDto.getColGrupos() != null) {
			for (Iterator it = visaoDto.getColGrupos().iterator(); it.hasNext();) {
				GrupoVisaoDTO grupoVisaoDTO = (GrupoVisaoDTO) it.next();
				if (grupoVisaoDTO.getColCamposVisao() != null) {
					for (Iterator it2 = grupoVisaoDTO.getColCamposVisao().iterator(); it2.hasNext();) {
						GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO = (GrupoVisaoCamposNegocioDTO) it2.next();
						String tam = "0";
						if (grupoVisaoCamposNegocioDTO.getTamanho() == null || grupoVisaoCamposNegocioDTO.getTamanho().intValue() == 0) {
							tam = "1";
						} else {
							Integer tamAux = 0;
							try {
								tamAux = new Integer(grupoVisaoCamposNegocioDTO.getTamanho());
							} catch (Exception e) {
							}
							tam = "" + (tamAux * 1);
						}
						out.println("		<th width='" + tam + "%'>" + grupoVisaoCamposNegocioDTO.getDescricaoNegocio() + "</th>");

						if (!tams.equalsIgnoreCase("")) {
							tams += ",";
						}
						tams += "{'sWidth': '" + tam + "%'}";
						i++;
					}
				}
			}
		}
		out.println("	</tr>");
		out.println("</thead>");

		out.println("<tbody>");
		out.println("</tbody>");
		out.println("</table>");
		out.println("</form>");

		if (visaoDto.getAcaoVisaoFilhaPesqRelacionada() == null) {
			visaoDto.setAcaoVisaoFilhaPesqRelacionada("");
		}

		String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/tableSearchVinc/tableSearchVinc.load?idVisao=" + visaoDto.getIdVisao()
				+ "&load=true&idVisaoRelacionada=" + visaoRelacionadaDTO.getIdVisaoRelacionada();
		out.println("<script>");
		out.println("function createTableVinc_" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "(urlParm, compl) {");
		out.println("    var url = urlParm;");
		out.println("    if (url == undefined){");
		out.println("    	url = '" + url + "';");
		out.println("    }");
		out.println("    url = url + compl;");
		out.println("    $('#" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "').dataTable( {");
		out.println("        'bProcessing': false,");
		out.println("        'bPaginate': false,");
		out.println("        'bDestroy': true,");
		out.println("        'bDeferRender': true,");
		out.println("        'sSearch': 'Busca:',");
		out.println("        'sEmptyTable': 'Não há dados para apresentar',");
		out.println("        'sLoadingRecords': 'Carregando...',");
		// out.println("        'bServerSide': true,");
		out.println("        'sServerMethod': 'POST', ");
		out.println("        'aoColumns': [" + tams + "], ");
		out.println("        'bFilter': false,");
		out.println("        'bInfo': false,");
		out.println("        'aaSorting': [[ 0, 'asc' ]],");
		out.println("        'sAjaxSource': url ");
		out.println("    } );");
		out.println("}");
		out.println("$(document).ready(function() {");
		out.println("    createTableVinc_" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "('" + url + "', '');");
		out.println("    $('#" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + " tbody tr').live('click', function() {" + "var nTds = $('td', this); " + "try{ "
				+ "TABLE_EDIT_CLICK(" + visaoDto.getIdVisao() + ", '" + visaoDto.getAcaoVisaoFilhaPesqRelacionada() + "', nTds, this);" + "}catch(e){}" + "} );");

		out.println("	$( '#TABLE_EDIT_" + visaoDto.getIdVisao() + "' ).dialog({");
		out.println("		title: '" + visaoDto.getDescricao() + "',");
		out.println("		width: 800,");
		out.println("		height: 600,");
		out.println("		modal: true,");
		out.println("		autoOpen: false,");
		out.println("		resizable: false,");
		out.println("		show: 'fade',");
		out.println("		hide: 'fade'");
		out.println("	});\n");

		out.println("	$('#" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "').dataTable().fnSetColumnVis( 0, false);");

		out.println("} );");
		out.println("</script>");

		out.println("<div id='TABLE_EDIT_" + visaoDto.getIdVisao() + "' style='display:none'>");
		out.println("<form name='formEdit" + visaoDto.getIdVisao() + "' id='formEdit" + visaoDto.getIdVisao() + "' action='" + Constantes.getValue("SERVER_ADDRESS")
				+ Constantes.getValue("CONTEXTO_APLICACAO") + "/pages/dinamicViews/dinamicViews'>");
		out.println("<input type='hidden' name='idVisaoEdit' value='" + visaoDto.getIdVisao() + "' />");
		out.println("<input type='hidden' name='jsonDataEdit' value='' />");
		out.println("<input type='hidden' name='rowIndexSel' value='' />");
		out.println("<input type='hidden' name='REMOVED' value='' />");
		String strArrayNamesColumns = MetaUtil.renderViewEdit(visaoDto, out, request, response);
		MetaUtil.generateButtonActionsTableEditVinc(visaoDto, out, strArrayNamesColumns);
		out.println("</form>");
		out.println("</div>");

		out.println("<script>");
		out.println("function ADICIONA_TABLE_SEARCH_" + visaoDto.getIdVisao() + "(){\n");
		out.println("    limparForm(document.formEdit" + visaoDto.getIdVisao() + ");");
		out.println("    document.formEdit" + visaoDto.getIdVisao() + ".rowIndexSel.value = '';");
		out.println("    trObj = null;");
		out.println("    $( '#TABLE_EDIT_" + visaoDto.getIdVisao() + "' ).dialog( 'open' );");
		out.println("}\n");
		out.println("</script>");
	}

	public static void generateButtonActionsTableEditVinc(VisaoDTO visaoDto, JspWriter out, String strArrayNamesColumns) throws IOException {
		out.println("<table>");
		out.println("<tr>");

		out.println("<td>");
		out.print("<button type='button' name='btnGravar_" + visaoDto.getIdVisao() + "' class='light' onclick='VINCULA_TABLE_EDIT(" + visaoDto.getIdVisao() + ", document.formEdit"
				+ visaoDto.getIdVisao() + ", \"" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "\", " + strArrayNamesColumns + ");'>");
		out.print("<img src='" + br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO") + "/template_new/images/icons/small/grey/pencil.png'>");
		out.print("<span>Vincular dados</span></button>");
		out.println("</td>");

		out.println("<td>");
		out.print("<button type='button' name='btnRemover_" + visaoDto.getIdVisao() + "' class='light' onclick='VINCULA_TABLE_EDIT_REMOVE(" + visaoDto.getIdVisao() + ", document.formEdit"
				+ visaoDto.getIdVisao() + ", \"" + VisaoRelacionadaDTO.PREFIXO_SISTEMA_TABELA_VINCULADA + visaoDto.getIdVisao() + "\", " + strArrayNamesColumns + ");'>");
		out.print("<img src='" + br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO") + "/template_new/images/icons/small/grey/trashcan.png'>");
		out.print("<span>Remover</span></button>");
		out.println("</td>");

		out.println("<td>");
		out.print("<button type='button' name='btnLimpar_" + visaoDto.getIdVisao() + "' class='light' onclick='limparForm(document.formEdit" + visaoDto.getIdVisao() + ");'>");
		out.print("<img src='" + br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO") + "/template_new/images/icons/small/grey/clear.png'>");
		out.print("<span>Limpar Dados</span></button>");
		out.println("</td>");

		out.println("<td>");
		out.print("<button type='button' name='btnLimpar_" + visaoDto.getIdVisao() + "' class='light' onclick=\"$( '#TABLE_EDIT_" + visaoDto.getIdVisao() + "' ).dialog( 'close' );\">");
		out.print("<span>Fechar</span></button>");
		out.println("</td>");

		out.println("</tr>");
		out.println("</table>");
	}

	public static String geraCampoHTML(GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO, HttpServletRequest request) throws ServiceException, Exception {
		String ret = "";
		String valid = "";
		String format = "";
		String descricao = grupoVisaoCamposNegocioDTO.getDescricaoNegocio();
		if (descricao != null) {
			descricao = descricao.replaceAll("\\[", "");
			descricao = descricao.replaceAll("\\]", "");
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(TEXT)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<input type='text' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
					+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " maxlength='" + grupoVisaoCamposNegocioDTO.getTamanho() + "' size='"
					+ grupoVisaoCamposNegocioDTO.getTamanho() + "'" + " class='" + valid + " Description[" + descricao + "]' />";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(TEXTAREA)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<textarea name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' "
					+ " rows='5' cols='" + grupoVisaoCamposNegocioDTO.getTamanho() + "'" + " class='" + valid + " Description[" + descricao + "]' style='border:1px solid black'></textarea>";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(DECIMAL)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}
			format = "Format[Money]";

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<input type='text' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
					+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " maxlength='" + grupoVisaoCamposNegocioDTO.getTamanho() + "' size='"
					+ grupoVisaoCamposNegocioDTO.getTamanho() + "'" + " class='" + valid + " " + format + " Description[" + descricao + "]' />";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(NUMBER)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}
			format = "Format[Numero]";

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<input type='text' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
					+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " maxlength='" + grupoVisaoCamposNegocioDTO.getTamanho() + "' size='"
					+ grupoVisaoCamposNegocioDTO.getTamanho() + "'" + " class='" + valid + " " + format + " Description[" + descricao + "]' />";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(HIDDEN)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<input type='hidden' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
					+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " class='" + valid + " Description[" + descricao + "]' value='' />";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(DATE)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Date,Required";
			} else {
				valid += "Date";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}
			ret = "<input type='text' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
					+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " maxlength='10' size='10' " + "class='" + valid + " Description[" + descricao + "] Format[Date]' />";
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RADIO)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}

			if (grupoVisaoCamposNegocioDTO.getColValores() != null) {
				for (Iterator it = grupoVisaoCamposNegocioDTO.getColValores().iterator(); it.hasNext();) {
					ValorVisaoCamposNegocioDTO valorVisaoCamposNegocioDTO = (ValorVisaoCamposNegocioDTO) it.next();
					ret += "<input type='radio' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='"
							+ grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " class='" + valid + " Description[" + descricao + "]' value='"
							+ valorVisaoCamposNegocioDTO.getValor() + "' />" + valorVisaoCamposNegocioDTO.getDescricao() + " ";
				}
			}
			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(CLASS_AND_METHOD)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}

			String compl = "";
			if (grupoVisaoCamposNegocioDTO.getTamanho() != null && grupoVisaoCamposNegocioDTO.getTamanho().intValue() > 0) {
				compl += " style='width: " + grupoVisaoCamposNegocioDTO.getTamanho() + "px' ";
			}

			ret += "<select name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' "
					+ " class='" + valid + " Description[" + descricao + "]' " + compl + ">";
			//Thiago Fernandes - 03/11/2013 - 14:10 - Sol. 121468 - Internacionalização.
			ret += "<option value=''>"+UtilI18N.internacionaliza(request, "citcorpore.comum.selecione")+"</option>";

			String formula = grupoVisaoCamposNegocioDTO.getFormula();
			if (formula == null) {
				formula = "";
			}
			String[] formulaArray = formula.split(",");
			try {
				if (formulaArray != null && formulaArray.length >= 4) {
					String nomeClasse = UtilStrings.nullToVazio(formulaArray[0]).trim();
					String nomeMetodo = UtilStrings.nullToVazio(formulaArray[1]).trim();
					String nomeAtributoID = UtilStrings.nullToVazio(formulaArray[2]).trim();
					String nomeAtributoDescr = UtilStrings.nullToVazio(formulaArray[3]).trim();

					Class classe = Class.forName(nomeClasse);
					Object objToExecute = classe.newInstance();
					Method metodo = Reflexao.findMethod(nomeMetodo, objToExecute);
					Collection colValores = (Collection) metodo.invoke(objToExecute, null);
					if (colValores != null) {
						for (Iterator it = colValores.iterator(); it.hasNext();) {
							Object objDto = (Object) it.next();
							Method metodoID = Reflexao.findMethod("get" + UtilStrings.convertePrimeiraLetra(nomeAtributoID, "U"), objDto);
							Method metodoDescr = Reflexao.findMethod("get" + UtilStrings.convertePrimeiraLetra(nomeAtributoDescr, "U"), objDto);

							Object objId = (Object) metodoID.invoke(objDto, null);
							Object objDescr = (Object) metodoDescr.invoke(objDto, null);

							ret += "<option value='" + objId.toString() + "'>" + objDescr.toString() + "</option>";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ret += "</select>";

			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(SELECT)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}

			String compl = "";
			if (grupoVisaoCamposNegocioDTO.getTamanho() != null && grupoVisaoCamposNegocioDTO.getTamanho().intValue() > 0) {
				compl += " style='width: " + grupoVisaoCamposNegocioDTO.getTamanho() + "px' ";
			}

			if (grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto() != null) {
				ret += "<select name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB()
						+ "' " + " class='" + valid + " Description[" + descricao + "]' " + compl + ">";
			}
			if (grupoVisaoCamposNegocioDTO.getColValores() != null) {
				for (Iterator it = grupoVisaoCamposNegocioDTO.getColValores().iterator(); it.hasNext();) {
					ValorVisaoCamposNegocioDTO valorVisaoCamposNegocioDTO = (ValorVisaoCamposNegocioDTO) it.next();
					ret += "<option value='" + valorVisaoCamposNegocioDTO.getValor() + "'>" + valorVisaoCamposNegocioDTO.getDescricao() + "</option>";
				}
			}

			ret += "</select>";

			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RELATION)) {
			String style = grupoVisaoCamposNegocioDTO.getTipoLigacao();
			if (style == null || style.equalsIgnoreCase("") || style.equalsIgnoreCase("C") || style.equalsIgnoreCase("N")) {
				style = "COMBO";
			} else if (style.equalsIgnoreCase("S") || style.equalsIgnoreCase("O")) {
				style = "LOOKUP";
			}
			if (style.equalsIgnoreCase("COMBO")) {
				if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
					valid += "Required";
				}

				if (!valid.equalsIgnoreCase("")) {
					valid = "Valid[" + valid + "]";
				}
				if (grupoVisaoCamposNegocioDTO.getTipoLigacao() == null) {
					grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
				}
				if (grupoVisaoCamposNegocioDTO.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioDTO.RELATION_NONE)) {
					grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
				}
				LookupService lookupService = (LookupService) ServiceLocator.getInstance().getService(LookupService.class, null);
				LookupDTO lookupDto = new LookupDTO();
				lookupDto.setTermoPesquisa("");
				lookupDto.setIdGrupoVisao(grupoVisaoCamposNegocioDTO.getIdGrupoVisao());
				lookupDto.setIdCamposObjetoNegocio(grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio());

				ret += "<select name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "'" + " class='" + valid + " Description[" + descricao + "]' >";
				//Thiago Fernandes - 03/11/2013 - 14:10 - Sol. 121468 - Internacionalização.
				ret += "<option value=''>"+UtilI18N.internacionaliza(request, "citcorpore.comum.selecione")+"</option>";
				Collection col = lookupService.findSimple(lookupDto);
				if (col != null) {
					for (Iterator it = col.iterator(); it.hasNext();) {
						ReturnLookupDTO returnLookupDTO = (ReturnLookupDTO) it.next();
						ret += "<option value='" + returnLookupDTO.getValue() + "'>" + returnLookupDTO.getLabel() + "</option>";
					}
				}
				ret += "</select>";
				return ret;
			}
			if (style.equalsIgnoreCase("LOOKUP")) {
				String url = Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO") + "/lookup/lookup.load";
				if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
					valid += "Required";
				}
				if (!valid.equalsIgnoreCase("")) {
					valid = "Valid[" + valid + "]";
				}
				if (grupoVisaoCamposNegocioDTO.getTipoLigacao() == null) {
					grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
				}
				if (grupoVisaoCamposNegocioDTO.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioDTO.RELATION_NONE)) {
					grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO);
				}
				ret += "<input name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB()
						+ "' class='" + valid + " Description[" + descricao + "]' style='width:400px'></input>";
				ret += generateScriptLookup(grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB(), url, grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio(),
						grupoVisaoCamposNegocioDTO.getIdGrupoVisao(), grupoVisaoCamposNegocioDTO.getTamanhoParaPesq(),request);
				return ret;
			}
		}
		return "";
	}

	public static String geraCampoGRID(GrupoVisaoCamposNegocioDTO grupoVisaoCamposNegocioDTO, HttpServletRequest request) throws ServiceException, Exception {
		String ret = "";
		String valid = "";
		String format = "";
		String descricao = grupoVisaoCamposNegocioDTO.getDescricaoNegocio();
		if (descricao != null) {
			descricao = descricao.replaceAll("\\[", "");
			descricao = descricao.replaceAll("\\]", "");
		}
		String options = "";
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(TEXT)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S") && (grupoVisaoCamposNegocioDTO.getTamanho() != null && grupoVisaoCamposNegocioDTO.getTamanho() > 0)) {
				options = ",options:{required:true, maxlength:" + grupoVisaoCamposNegocioDTO.getTamanho() + "}";
			} else if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options = ",options:{required:true}";
			}
			options = "editor:{type:'text'" + options + "}";
			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(TEXTAREA)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options = ",options:{required:true}";
			}
			options = "editor:{type:'textarea'" + options + "}";
			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(DECIMAL)) {
			options = ",options:{";
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options += "required:true,";
			}
			options += "precision:2,decimalSeparator:',',groupSeparator:''}";
			options = "editor:{type:'numberbox'" + options + "}";
			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(NUMBER)) {
			options = ",options:{";
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options += "required:true,";
			}
			options += "decimalSeparator:',',groupSeparator:''}";
			options = "editor:{type:'numberbox'" + options + "}";
			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(HIDDEN)) {
			return "";
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(DATE)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options = ",options:{required:true}";
			}
			options = "editor:{type:'datebox'" + options + "}";
			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RADIO)) {

			options = "options:{";
			options += "valueField:'id',";
			options += "textField:'desc',";
			options += "editable:false,";
			options += "panelHeight:'auto',";
			options += "data:array_" + grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio();
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options += ",required:true";
			}
			options += "}";

			options = "editor:{type:'combobox'," + options + "}";

			return options;

			/*
			 * if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")){ valid += "Required"; }
			 * 
			 * if (!valid.equalsIgnoreCase("")){ valid = "Valid[" + valid + "]"; }
			 * 
			 * if (grupoVisaoCamposNegocioDTO.getColValores() != null){ for(Iterator it = grupoVisaoCamposNegocioDTO.getColValores().iterator(); it.hasNext();){ ValorVisaoCamposNegocioDTO
			 * valorVisaoCamposNegocioDTO = (ValorVisaoCamposNegocioDTO)it.next(); ret += "<input type='radio' name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " +
			 * " id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " class='" + valid + " Description[" + descricao + "]' value='" +
			 * valorVisaoCamposNegocioDTO.getValor() + "' />" + valorVisaoCamposNegocioDTO.getDescricao() + " "; } } return ret;
			 */
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(CLASS_AND_METHOD)) {
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				valid += "Required";
			}

			if (!valid.equalsIgnoreCase("")) {
				valid = "Valid[" + valid + "]";
			}

			String compl = "";
			if (grupoVisaoCamposNegocioDTO.getTamanho() != null && grupoVisaoCamposNegocioDTO.getTamanho().intValue() > 0) {
				compl += " style='width: " + grupoVisaoCamposNegocioDTO.getTamanho() + "px' ";
			}

			ret += "<select name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' " + " id='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "' "
					+ " class='" + valid + " Description[" + descricao + "]' " + compl + ">";
			//Thiago Fernandes - 03/11/2013 - 14:10 - Sol. 121468 - Internacionalização.
			ret += "<option value=''>"+UtilI18N.internacionaliza(request, "citcorpore.comum.selecione")+"</option>";

			String formula = grupoVisaoCamposNegocioDTO.getFormula();
			if (formula == null) {
				formula = "";
			}
			String[] formulaArray = formula.split(",");
			try {
				if (formulaArray != null && formulaArray.length >= 4) {
					String nomeClasse = UtilStrings.nullToVazio(formulaArray[0]).trim();
					String nomeMetodo = UtilStrings.nullToVazio(formulaArray[1]).trim();
					String nomeAtributoID = UtilStrings.nullToVazio(formulaArray[2]).trim();
					String nomeAtributoDescr = UtilStrings.nullToVazio(formulaArray[3]).trim();

					Class classe = Class.forName(nomeClasse);
					Object objToExecute = classe.newInstance();
					Method metodo = Reflexao.findMethod(nomeMetodo, objToExecute);
					Collection colValores = (Collection) metodo.invoke(objToExecute, null);
					if (colValores != null) {
						for (Iterator it = colValores.iterator(); it.hasNext();) {
							Object objDto = (Object) it.next();
							Method metodoID = Reflexao.findMethod("get" + UtilStrings.convertePrimeiraLetra(nomeAtributoID, "U"), objDto);
							Method metodoDescr = Reflexao.findMethod("get" + UtilStrings.convertePrimeiraLetra(nomeAtributoDescr, "U"), objDto);

							Object objId = (Object) metodoID.invoke(objDto, null);
							Object objDescr = (Object) metodoDescr.invoke(objDto, null);

							ret += "<option value='" + objId.toString() + "'>" + objDescr.toString() + "</option>";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ret += "</select>";

			return ret;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(SELECT)) {
			options = "options:{";
			options += "valueField:'id',";
			options += "textField:'desc',";
			options += "editable:false,";
			options += "panelHeight:'auto',";
			options += "data:array_" + grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio() + "";
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options += ",required:true";
			}
			options += "}";

			options = "editor:{type:'combobox'," + options + "}";

			return options;
		}
		if (grupoVisaoCamposNegocioDTO.getTipoNegocio().equalsIgnoreCase(RELATION)) {
			options = "options:{";
			options += "valueField:'id',";
			options += "textField:'desc',";
			options += "editable:false,";
			options += "panelHeight:'auto',";
			options += "data:array_" + grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio() + "";
			if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")) {
				options += ",required:true";
			}
			options += "}";

			options = "editor:{type:'combobox'," + options + "}";

			return options;

			/*
			 * if (grupoVisaoCamposNegocioDTO.getObrigatorio().equalsIgnoreCase("S")){ valid += "Required"; }
			 * 
			 * if (!valid.equalsIgnoreCase("")){ valid = "Valid[" + valid + "]"; } if (grupoVisaoCamposNegocioDTO.getTipoLigacao() == null){
			 * grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO); } if
			 * (grupoVisaoCamposNegocioDTO.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioDTO.RELATION_NONE)){
			 * grupoVisaoCamposNegocioDTO.setTipoLigacao(GrupoVisaoCamposNegocioDTO.RELATION_COMBO); } LookupService lookupService = (LookupService)
			 * ServiceLocator.getInstance().getService(LookupService.class, null); LookupDTO lookupDto = new LookupDTO(); lookupDto.setTermoPesquisa("");
			 * lookupDto.setIdGrupoVisao(grupoVisaoCamposNegocioDTO.getIdGrupoVisao()); lookupDto.setIdCamposObjetoNegocio(grupoVisaoCamposNegocioDTO.getIdCamposObjetoNegocio());
			 * 
			 * ret += "<select name='" + grupoVisaoCamposNegocioDTO.getCamposObjetoNegocioDto().getNomeDB() + "'" + " class='" + valid + " Description[" + descricao + "]' >"; ret +=
			 * "<option value=''>-- Selecione --</option>"; Collection col = lookupService.findSimple(lookupDto); if (col != null){ for (Iterator it = col.iterator(); it.hasNext();){ ReturnLookupDTO
			 * returnLookupDTO = (ReturnLookupDTO)it.next(); ret += "<option value='" + returnLookupDTO.getValue() + "'>" + returnLookupDTO.getLabel() + "</option>"; } } ret += "</select>"; return
			 * ret;
			 */
		}
		return "";
	}

	private static String generateScriptLookup(String name, String url, Integer idCamposObjetoNegocio, Integer idGrupoVisao, Integer tamMaxParaPesq, HttpServletRequest request) throws ServiceException, Exception {
		String ret = "";

		HashMap map = new HashMap();
		String urlAux = url + "?lookupName=" + name + "&idCamposObjetoNegocio=" + idCamposObjetoNegocio + "&idGrupoVisao=" + idGrupoVisao + "";

		GrupoVisaoCamposNegocioLigacaoService grupoVisaoCamposNegocioLigacaoService = (GrupoVisaoCamposNegocioLigacaoService) ServiceLocator.getInstance().getService(
				GrupoVisaoCamposNegocioLigacaoService.class, null);
		CamposObjetoNegocioService camposObjetoNegocioService = (CamposObjetoNegocioService) ServiceLocator.getInstance().getService(CamposObjetoNegocioService.class, null);
		ObjetoNegocioService objetoNegocioService = (ObjetoNegocioService) ServiceLocator.getInstance().getService(ObjetoNegocioService.class, null);
		CamposObjetoNegocioDTO camposObjetoNegocioDTO = null;

		Collection colPresentation = new ArrayList();
		Collection colValue = new ArrayList();
		Collection colItens = grupoVisaoCamposNegocioLigacaoService.findByIdGrupoVisaoAndIdCamposObjetoNegocio(idGrupoVisao, idCamposObjetoNegocio);
		if (colItens != null) {
			for (Iterator it = colItens.iterator(); it.hasNext();) {
				GrupoVisaoCamposNegocioLigacaoDTO grupoVisaoCamposNegocioLigacaoAux = (GrupoVisaoCamposNegocioLigacaoDTO) it.next();

				if (grupoVisaoCamposNegocioLigacaoAux != null && grupoVisaoCamposNegocioLigacaoAux.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioLigacaoDTO.PRESENTATION)) {
					camposObjetoNegocioDTO = new CamposObjetoNegocioDTO();
					camposObjetoNegocioDTO.setIdCamposObjetoNegocio(grupoVisaoCamposNegocioLigacaoAux.getIdCamposObjetoNegocioLigacao());
					try {
						camposObjetoNegocioDTO = (CamposObjetoNegocioDTO) camposObjetoNegocioService.restore(camposObjetoNegocioDTO);
					} catch (Exception e) {
						e.printStackTrace();
						camposObjetoNegocioDTO = null;
					}
					if (camposObjetoNegocioDTO != null) {
						camposObjetoNegocioDTO.setDescricao(grupoVisaoCamposNegocioLigacaoAux.getDescricao());
						colPresentation.add(camposObjetoNegocioDTO);
					}
				}
				if (grupoVisaoCamposNegocioLigacaoAux != null && grupoVisaoCamposNegocioLigacaoAux.getTipoLigacao().equalsIgnoreCase(GrupoVisaoCamposNegocioLigacaoDTO.VALUE)) {
					if (grupoVisaoCamposNegocioLigacaoAux != null) {
						camposObjetoNegocioDTO = new CamposObjetoNegocioDTO();
						camposObjetoNegocioDTO.setIdCamposObjetoNegocio(grupoVisaoCamposNegocioLigacaoAux.getIdCamposObjetoNegocioLigacao());
						try {
							camposObjetoNegocioDTO = (CamposObjetoNegocioDTO) camposObjetoNegocioService.restore(camposObjetoNegocioDTO);
						} catch (Exception e) {
							e.printStackTrace();
							camposObjetoNegocioDTO = null;
						}
						if (camposObjetoNegocioDTO != null) {
							camposObjetoNegocioDTO.setDescricao(UtilI18N.internacionaliza(request, "centroResultado.codigo"));
							colValue.add(camposObjetoNegocioDTO);
						}
					}
				}
			}
		}

		String columns = "";
		String primeiroCampoId = "";
		String primeiroCampoDesc = "";
		if (colValue != null) {
			for (Iterator it = colValue.iterator(); it.hasNext();) {
				camposObjetoNegocioDTO = (CamposObjetoNegocioDTO) it.next();
				if (!columns.trim().equalsIgnoreCase("")) {
					columns += ",";
				}
				columns += "{field:'" + camposObjetoNegocioDTO.getNomeDB() + "',title:'" + camposObjetoNegocioDTO.getDescricao() + "',width:50}";
				if (primeiroCampoId.equalsIgnoreCase("")) {
					primeiroCampoId = camposObjetoNegocioDTO.getNomeDB();
				}
			}
		}
		if (colPresentation != null) {
			for (Iterator it = colPresentation.iterator(); it.hasNext();) {
				camposObjetoNegocioDTO = (CamposObjetoNegocioDTO) it.next();
				if (!columns.trim().equalsIgnoreCase("")) {
					columns += ",";
				}
				columns += "{field:'" + camposObjetoNegocioDTO.getNomeDB() + "',title:'" + camposObjetoNegocioDTO.getDescricao() + "',width:200}";
				if (primeiroCampoDesc.equalsIgnoreCase("")) {
					primeiroCampoDesc = camposObjetoNegocioDTO.getNomeDB();
				}
			}
		}

		ret += "<script>";
		ret += "$('#" + name + "').combogrid({";
		ret += "	panelWidth:600,";
		ret += "	url: '" + urlAux + "',";
		ret += "	onSelect: function(rec){";
		ret += "	    var r = $('#" + name + "').combogrid('grid').datagrid('getSelected');";
		ret += "	    document.getElementById('" + primeiroCampoId + "').value = r." + primeiroCampoId + ";";
		ret += "	},";
		ret += "	idField:'" + primeiroCampoId + "',";
		ret += "	textField:'" + primeiroCampoDesc + "',";
		ret += "	mode:'remote',";
		ret += "	fitColumns:true,";
		ret += "	columns:[[";
		ret += "		" + columns;
		ret += "	]]";
		ret += "});\n";
		ret += "function " + name + "_limparField(){";
		ret += "$('#" + name + "').combogrid('clear');";
		ret += "}\n";
		ret += "</script>";

		return ret;
		/*
		 * String ret = "<script>"; ret += "function qq_" + idCamposObjetoNegocio + "(value,name){"; ret += "    alert(value+\":\"+name)"; ret += "}"; ret += "</script>";
		 * 
		 * ret += "<input id=\"ss" + idCamposObjetoNegocio + "\" class=\"easyui-searchbox\" style=\"width:300px\""; ret += "        data-options=\"searcher:_" + idCamposObjetoNegocio +
		 * ",prompt:'Informe o valor de pesquisa',menu:'#mm_" + idCamposObjetoNegocio + "'\"></input>"; ret += "<div id=\"mm_" + idCamposObjetoNegocio + "\" style=\"width:120px\">"; ret +=
		 * "<div data-options=\"name:'all',iconCls:'icon-ok'\">Em qualquer parte do texto</div>"; ret += "<div data-options=\"name:'sports'\">Somente no início</div>"; ret += "</div>";
		 * 
		 * return ret;
		 */
		/*
		 * String ret = "<script>";
		 * 
		 * String maxLen = "0"; if (tamMaxParaPesq != null){ maxLen = "" + tamMaxParaPesq; }
		 * 
		 * ret += "$(function() {" + "$('#" + name + "_remotejsonp').button().lookup({" + "source: function(request, response) { " + "$.ajax({" + "url: '" + url + "', " + "dataType: 'json', " +
		 * "data: { " + "    lookupName: '" + name + "', " + "    idCamposObjetoNegocio: '" + idCamposObjetoNegocio + "', " + "    idGrupoVisao: '" + idGrupoVisao + "', " + "    featureClass: 'P', " +
		 * "    style: 'full'," + "    maxRows: 12, " + "    termoPesquisa: request.term " + "}, " + "success: function(data) { " + "    response($.map(data.values, function(item) { " +
		 * "        return { " + "            label: item.label, " + "            value: item.value " + "        } " + "    })); " + "} " + "}); " + "}, " + "minLength: " + maxLen + ", " +
		 * "cancelText: 'Fechar', " + "disabled: false, " + "modal: true, " + "okText: 'Selecionar', " + "title: 'Localizar', " + "select: function(item) { " + "    $('#" + name +
		 * "').val(item.value); " + "    $('#" + name + "_label').val(item.label); " + "}, " + "open: function() { " + "    $(this).removeClass('ui-corner-all').addClass('ui-corner-top'); " + "}, " +
		 * "close: function() { " + "    $(this).removeClass('ui-corner-top').addClass('ui-corner-all'); " + "} " + "}); " + "});";
		 * 
		 * ret += "</script>";
		 * 
		 * return ret;
		 */
	}

	public static boolean isDateType(String typeDB) {
		if (typeDB == null) {
			return false;
		}
		String typeDBAux = typeDB;
		typeDBAux = typeDBAux.toUpperCase();
		if (typeDBAux.startsWith("DATE")) {
			return true;
		}
		return false;
	}

	public static boolean isStringType(String typeDB) {
		if (typeDB == null) {
			return false;
		}
		String typeDBAux = typeDB;
		typeDBAux = typeDBAux.toUpperCase();
		if (typeDBAux.startsWith("VARCHAR") || typeDBAux.startsWith("CHAR") || typeDBAux.startsWith("TEXT") || typeDBAux.startsWith("BPCHAR") || typeDBAux.startsWith("CLOB")
				|| typeDBAux.startsWith("STRING")) {
			return true;
		}
		return false;
	}

	public static boolean isNumericType(String typeDB) {
		if (typeDB == null) {
			return false;
		}
		String typeDBAux = typeDB;
		typeDBAux = typeDBAux.toUpperCase();
		if (typeDBAux.startsWith("INT") || typeDBAux.startsWith("LONG") || typeDBAux.startsWith("MONEY") || typeDBAux.startsWith("DOUBLE") || typeDBAux.startsWith("DECIMAL")
				|| typeDBAux.startsWith("NUMBER") || typeDBAux.startsWith("BIGINT") || typeDBAux.startsWith("INTEGER") || typeDBAux.startsWith("SMALLINT") || typeDBAux.startsWith("REAL")
				|| typeDBAux.startsWith("NUMERIC")) {
			return true;
		}
		return false;
	}

	public static Object convertType(String typeDBParm, String value) {
		if (typeDBParm == null) {
			return null;
		}
		if (value != null){
			value = value.trim();
		}
		String typeDB = typeDBParm;
		typeDB = typeDB.toUpperCase();
		if (typeDB.startsWith("VARCHAR") || typeDB.startsWith("CHAR") || typeDB.startsWith("TEXT") || typeDB.startsWith("MEDIUMTEXT") || typeDB.startsWith("CLOB") || typeDB.startsWith("BPCHAR")
				|| typeDB.startsWith("STRING") || typeDB.startsWith("LONGTEXT")) {
			return value;
		}
		if (typeDB.startsWith("INT") || typeDB.startsWith("BIGINT") || typeDB.startsWith("INTEGER") || typeDB.startsWith("SMALLINT")) {
			Integer val = null;
			try {
				String valueAux = new String(value);
				if (value != null) {
					int indexAux = value.indexOf("["); // Trata algumas coisas da DinamicView
					if (indexAux > -1) {
						valueAux = valueAux.substring(indexAux);
					}
				}
				valueAux = UtilStrings.apenasNumeros(valueAux);
				if (!valueAux.equalsIgnoreCase("")) {
					val = new Integer(valueAux);
				} else {
					val = null;
				}
			} catch (Exception e) {
				System.out.println("Problemas ao converter tipo de dados: Funcao convertType: " + typeDB + " --> " + value);
				e.printStackTrace();
				val = null;
			}
			return val;
		}
		if (typeDB.startsWith("LONG") || typeDB.startsWith("BIGLONG")) {
			Long val = null;
			try {
				val = new Long(value);
			} catch (Exception e) {
				e.printStackTrace();
				val = null;
			}
			return val;
		}
		if (typeDB.startsWith("DATE") || typeDB.startsWith("DATA")) {
			Date data = null;

			try {
				data = new Date(UtilDatas.strToSQLDate(((String) value)).getTime());
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return data;
		}
		if (typeDB.startsWith("MONEY") || typeDB.startsWith("DOUBLE") || typeDB.startsWith("DECIMAL") || typeDB.startsWith("NUMERIC") || typeDB.startsWith("NUMBER") || typeDB.startsWith("REAL")
				|| typeDB.startsWith("FLOAT")) {
			int posicaoPonto = value.indexOf(".");
			if (posicaoPonto > -1 && posicaoPonto == (value.length() - 3)) { // deve ser -3 mesmo, inclui a posicao do ponto.
				// ESTA NO PADRAO AMERICANO. PONTO NO LUGAR DE VIRGULA.
				String aux = value;
				aux = aux.replaceAll("\\,", "");

				Double duplo = null;
				try {
					duplo = new Double(Double.parseDouble(aux));
				} catch (Exception e) {
					// e.printStackTrace();
					duplo = null;
				}
				return duplo;
			} else {
				String aux = value;
				aux = aux.replaceAll("\\.", "");
				aux = aux.replaceAll("\\,", "\\.");

				Double duplo = null;
				try {
					duplo = new Double(Double.parseDouble(aux));
				} catch (Exception e) {
					// e.printStackTrace();
					duplo = null;
				}
				return duplo;
			}
		}
		return null;
	}

	public static Object convertType(String typeDBParm, String value, Integer precisionDBParm) {
		if (typeDBParm == null) {
			return null;
		}

		if (precisionDBParm == null) {
			precisionDBParm = 0;
		}

		value = value.trim();
		String typeDB = typeDBParm;
		typeDB = typeDB.toUpperCase();
		if (typeDB.startsWith("VARCHAR") || typeDB.startsWith("CHAR") || typeDB.startsWith("TEXT") || typeDB.startsWith("MEDIUMTEXT") || typeDB.startsWith("CLOB") || typeDB.startsWith("BPCHAR")
				|| typeDB.startsWith("STRING") || typeDB.startsWith("LONGTEXT")) {
			return value;
		}
		if (typeDB.startsWith("INT") || typeDB.startsWith("BIGINT") || typeDB.startsWith("INTEGER") || typeDB.startsWith("SMALLINT") || (typeDB.startsWith("NUMBER") && precisionDBParm == 0)) {
			Integer val = null;
			try {
				String valueAux = new String(value);
				if (value != null) {
					int indexAux = value.indexOf("["); // Trata algumas coisas da DinamicView
					if (indexAux > -1) {
						valueAux = valueAux.substring(indexAux);
					}
				}
				valueAux = UtilStrings.apenasNumeros(valueAux);
				if (!valueAux.equalsIgnoreCase(""))
					val = new Integer(valueAux);
			} catch (Exception e) {
				System.out.println("Problemas ao converter o tipo de dados: Funcao convertType: " + typeDB + " --> " + value);
				e.printStackTrace();
				val = null;
			}
			return val;
		}
		if (typeDB.startsWith("LONG") || typeDB.startsWith("BIGLONG")) {
			Long val = null;
			try {
				val = new Long(value);
			} catch (Exception e) {
				e.printStackTrace();
				val = null;
			}
			return val;
		}
		if (typeDB.startsWith("TIMESTAMP")) {
			Timestamp val = new Timestamp(System.currentTimeMillis());
			java.util.Date data = null;
			try {
				if (UtilDatas.isThisDateValid(value, "dd/MM/yyyy")) {
					val = UtilDatas.strToTimestamp(value);
				} else
					val = Timestamp.valueOf(value);
			} catch (Exception e) {
				e.printStackTrace();
				val = null;
			}
			return val;
		}

		if (typeDB.startsWith("DATE") || typeDB.startsWith("DATA")) {
			Date data = null;

			try {
				data = new Date(UtilDatas.strToSQLDate(((String) value)).getTime());
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return data;
		}
		if (typeDB.startsWith("MONEY") || typeDB.startsWith("DOUBLE") || typeDB.startsWith("DECIMAL") || typeDB.startsWith("NUMERIC") || typeDB.startsWith("NUMBER") || typeDB.startsWith("REAL")
				|| typeDB.startsWith("FLOAT")) {
			int posicaoPonto = value.indexOf(".");
			if (posicaoPonto > -1 && posicaoPonto == (value.length() - 3)) { // deve ser -3 mesmo, inclui a posicao do ponto.
				// ESTA NO PADRAO AMERICANO. PONTO NO LUGAR DE VIRGULA.
				String aux = value;
				aux = aux.replaceAll("\\,", "");

				Double duplo = null;
				try {
					duplo = new Double(Double.parseDouble(aux));
				} catch (Exception e) {
					// e.printStackTrace();
					duplo = null;
				}
				return duplo;
			} else {
				String aux = value;
				aux = aux.replaceAll("\\.", "");
				aux = aux.replaceAll("\\,", "\\.");

				Double duplo = null;
				try {
					duplo = new Double(Double.parseDouble(aux));
				} catch (Exception e) {
					// e.printStackTrace();
					duplo = null;
				}
				return duplo;
			}
		}
		return null;
	}
}
