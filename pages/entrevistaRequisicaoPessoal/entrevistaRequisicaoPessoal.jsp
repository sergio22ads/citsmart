<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/tags/cit" prefix="cit"%>
<%@page import="br.com.centralit.citcorpore.util.WebUtil"%>
<%@page import="br.com.centralit.citcorpore.bean.UsuarioDTO"%>
<%@page import="br.com.citframework.dto.Usuario"%>
<%@page import="br.com.centralit.citcorpore.util.CitCorporeConstantes"%>
<%@ taglib uri="/tags/i18n" prefix="i18n"%>


<html>
	<head>	
		<%
	response.setHeader( "Cache-Control", "no-cache");
	response.setHeader( "Pragma", "no-cache");
	response.setDateHeader ( "Expires", -1);
	String id = request.getParameter("id");

	%>
		<%@include file="/novoLayout/common/include/libCabecalho.jsp" %>
		<script src="../../novoLayout/common/include/js/entrevistaRequisicaoPessoal.js"></script>
		<link type="text/css" rel="stylesheet" href="../../novoLayout/common/include/css/entrevistaRequisicaoPessoal.css"/>
		<link type="text/css" rel="stylesheet" href="../../novoLayout/common/include/css/template.css"/>
		
		<style type="text/css">
			#modal_curriculo {
				width: 99% !important;
				margin-left: -49%;
				top: 1% !important;
				margin-top: 0px !important;
			}
			
			#modal_curriculo .modal-body {
				max-height: 705px !important;
				overflow: auto !important;
			}
		</style>
		<script type="text/javascript">

		 function tooltip() {	
			  $(document).ready(function() {$('.titulo').tooltip()});
		  }
		  
		  function gerarImgExclusaoCurriculo(row, obj) {
		   		/* row.cells[2].innerHTML = "<a class='close' data-dismiss='alert'></a><p>Candidato est� na Lista Negra</p>"; */

			     row.cells[3].innerHTML = '<a class="btn-action glyphicons edit btn-success titulo" title="<i18n:message key="entrevistaRequisicaoPessoal.abrirJanelaEntrevista"/>" onmouseover="tooltip();" onclick="abrePopupEntrevista('+obj.idCurriculo+','+obj.idTriagem+',\''+obj.tipoEntrevista+'\')"><i></i></a> | ' 
		         row.cells[3].innerHTML += '<a class="btn-action glyphicons nameplate btn-success titulo" title="<i18n:message key="entrevistaRequisicaoPessoal.abrirJanelaCurriculo"/>" onmouseover="tooltip();" onclick="abrePopupCurriculo('+obj.idCurriculo+')"><i></i></a> | ' ;
		         row.cells[3].innerHTML += '<a href="#" class="btn-action glyphicons history btn-success titulo" title="Abrir janela hist�rico do candidato" onmouseover="tooltip();" onclick="abrePopupHistoricoCandidato('+obj.idCurriculo+')"><i></i></a>';
				
		       if (obj.caminhoFoto != "")
		       	row.cells[0].innerHTML = '<img src="' + obj.caminhoFoto + '" border=0 width="128px" />';

		       else
		    	row.cells[0].innerHTML = '<div class="col_100"><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/novoLayout/common/theme/images/avatar.jpg" border=0  /></div>';
		  }

		  function abrePopupCurriculo(idCurriculo){
			  	document.getElementById('frameCurriculo').src = URL_SISTEMA+"pages/templateCurriculo/templateCurriculo.load?iframe=true&idCurriculo=" + idCurriculo ;
				$("#modal_curriculo").modal("show");
			}

		  function abrePopupEntrevista(idCurriculo, idTriagem, tipoEntrevista) {
				var idSolicitacao = document.form.idSolicitacaoServico.value;
				document.getElementById('frameFicha').src =URL_SISTEMA+"pages/entrevistaCandidato/entrevistaCandidato.load?idCurriculo=" + idCurriculo + '&idTriagem=' + idTriagem + '&tipoEntrevista=' + tipoEntrevista + '&idSolicitacaoServico=' + idSolicitacao;
				$("#modal_ficha").modal("show");
			}

		  function abrePopupHistoricoCandidato(idCurriculo) { 
	    	  /* document.formSugestaoCurriculos.idSolicitacaoServico.value = document.form.idSolicitacaoServico.value; */
	    	  var URL_SISTEMA = '<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/';
	    	  document.getElementById("frameHistoricoCandidato").src =  URL_SISTEMA+'pages/historicoCandidato/historicoCandidato.load?iframe=true&idCurriculo=' + idCurriculo;
	    	  $("#modal_historicoCandidato").modal("show"); 
	      }
		</script>
	
	</head>
	<body>
		<div class="container-fluid fixed ">
	
			<div id="wrapper">
					
				<!-- Inicio conteudo -->
				
					<div class="box-generic">
						<form name='form' action='<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/pages/entrevistaRequisicaoPessoal/entrevistaRequisicaoPessoal'>
				      	<input type='hidden' name='idSolicitacaoServico' id='idSolicitacaoServico'/>
						<input type='hidden' name='acao' id='acao'/> 
						<input type='hidden' name='idTarefa' id='idTarefa'/>
						<input type='hidden' name='serializeTriagem' id='serializeTriagem'/>
						<input type='hidden' name='preRequisitoEntrevistaGestor' id='preRequisitoEntrevistaGestor' /> 
						<input type='hidden' name='qtdCandidatosAprovados' id='qtdCandidatosAprovados' />
						<div class="row-fluid">
							<div class='span4'>
								<label ><i18n:message key="cargo.cargo" /></label>
								 <select name='idCargo' id='idCargo' class="Valid[Required] Description[lookup.cargo]" disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.numeroVagas" /></label>
								<input type='text' name='vagas' size='10' maxlength="100" readonly="readonly"/>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.vagaConfidencial" /></label>
								 <select name='confidencial' class="Valid[Required] Description[requisicaoPessoal.vagaConfidencial]" disabled="disabled">
	                             	<option value=" "><i18n:message key='citcorpore.comum.selecione'/></option>
	                                <option value="S"><i18n:message key='citcorpore.comum.sim'/></option>
	                                <option value="N"><i18n:message key='citcorpore.comum.nao'/></option>
                                 </select>
							</div>
						</div>
						<div class="row-fluid">
							<div class='span4'>
								<label  ><i18n:message key="requisicaoPessoal.tipoContratacao" /></label>
								<select name='tipoContratacao' class="Valid[Required] Description[requisicaoPessoal.tipoContratacao]" disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.motivoContratacao" /></label>
								<select name='motivoContratacao' class="Valid[Required] Description[requisicaoPessoal.tipoContratacao]" disabled="disabled">
                               	      <option value=''><i18n:message key='citcorpore.comum.selecione'/></option>
                               	      <option value='N'><i18n:message key='analiseRequisicaoPessoal.novoCargo'/></option>
                               	      <option value='D'><i18n:message key='analiseRequisicaoPessoal.demissaoPessoal'/></option>
                               	      <option value='A'><i18n:message key='analiseRequisicaoPessoal.aumentoDemanda'/></option>
                               	      <option value='R'><i18n:message key='analiseRequisicaoPessoal.requisicaoCliente'/></option>
                                </select>
							</div>
							<div class='span4'>
							 <label ><i18n:message key="requisicaoPessoal.salario" /></label>
								<div class='row-fluid'>
									<div class='span6'>
										<input style="cursor: pointer" type="checkbox" name="salarioACombinar" value="S" disabled="disabled">
		                                <i18n:message key="requisicaoPessoal.salarioACombinar" />
	                                 </div>
	                                 <div class='span6'>
		                                 <div id='divPainelSalario' style='display:block'>
		                                      <div class="input-prepend input-append">
													<span class="add-on"><i18n:message key='citcorpore.comum.simboloMonetario'/></span>
													<input type='text' name='salario' size='10' maxlength="100" class='Format[Moeda] span10' readonly="readonly"/>
											  </div>
			                         	</div>
		                         	</div>
	                         	</div>
							</div>
						</div>
						<div class="row-fluid">
							<div class='span4'>
								<label><i18n:message key="unidade.pais" /></label>
								<select name='idPais' id="idPais"  onchange="document.form.fireEvent('preencherComboUfs');" class="Description[unidade.pais]"  disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="localidade.uf" /></label>
								<select name='idUf' id="idUf" onchange="document.form.fireEvent('preencherComboCidade');" class="Description[uf]"  disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="localidade.cidade" /></label>
								<select id="idCidade" name='idCidade'  class="Description[localidade.cidade]" disabled="disabled"></select>
							</div>
						</div>
						
						<div class="row-fluid">
							<div class='span4'>
								<label ><i18n:message key="citcorporeRelatorio.comum.lotacao" /></label>
								<select name='idUnidade' class="Valid[Required] Description[citcorporeRelatorio.comum.lotacao]" disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label><i18n:message key="requisicaoPessoal.beneficios" /></label>
								<textarea name='beneficios' rows="4"  class='span8' readonly="readonly"></textarea>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.horarios" /></label>
								<select name='idJornada' id='idJornada'  disabled="disabled"></select>
							</div>
						</div>
						<div class="row-fluid">
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.centroCusto" /></label>
								<select name='idCentroCusto' class="Valid[Required] Description[requisicaoPessoal.centroCusto]" disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.projeto" /></label>
								<select name='idProjeto' class="Valid[Required] Description[requisicaoPessoal.projeto]" disabled="disabled"></select>
							</div>
							<div class='span4'>
								<label ><i18n:message key="requisicaoPessoal.folgas" /></label>
								<input type='text' name='folgas' size='10' maxlength="100" readonly="readonly"/>
							</div>
						</div>
						
						<div class="widget">
							<div class="widget-head">
								<h5 id="tituloEntrevistasPendentes" class="heading"><i18n:message key="entrevistaRequisicaoPessoal.entrevistaPendente"/></h5>
								</div>
							<!-- Tabela de resultados -->
							<div class="widget-body">
								
									 <table id="tblCurriculos" class="table  table-condensed" style="overflow: auto">
					                       <tr>
					                           <th width="130px"><i18n:message key="rh.foto"/></th>
					                           <th><i18n:message key="produto.detalhes"/></th>
					                           <th width="250px"><i18n:message key="citcorpore.comum.informacao"/></th>
					                           <th ><i18n:message key="questionario.acoes"/></th>
					                       </tr>
			                   		</table>
								
							</div>
						</div>
					</form>
				  </div>
				  <div class="modal hide fade in" id="modal_ficha" aria-hidden="false">
						<!-- Modal heading -->
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
							<h3></h3>
						</div>
						<!-- // Modal heading END -->
						<!-- Modal body -->
						<div class="modal-body">
							<form name='formSugestaoCurriculos' id='formSugestaoCurriculos'>
		        					<input type='hidden' name='idSolicitacaoServico' id='idSolicitacaoServico'/>
		           				<div>
		           					<iframe width="99%" height="600" id='frameFicha' name='frameFicha' src=""></iframe>
		           				</div>
	    					</form>
						</div>
						<!-- // Modal body END -->
						<!-- Modal footer -->
						<div class="modal-footer">
							<div style="margin: 0;" class="form-actions">
								<a href="#" class="btn " data-dismiss="modal"><i18n:message key="citcorpore.comum.fechar" /></a> 
							</div>
						<!-- // Modal footer END -->
					</div>
				</div>
				
				  <div class="modal hide fade in" id="modal_curriculo" aria-hidden="false">
						<!-- Modal heading -->
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
							<h3></h3>
						</div>
						<!-- // Modal heading END -->
						<!-- Modal body -->
						<div class="modal-body">
							<form name='formCurriculo' id='formCurriculo'>
		        					<input type='hidden' name='idSolicitacaoServico' id='idSolicitacaoSugestao'/>
		           				<div>
		           					<iframe width="99%" height="600" id='frameCurriculo' name='frameCurriculo' src=""></iframe>
		           				</div>
	    					</form>
						</div>
						<!-- // Modal body END -->
						<!-- Modal footer -->
						<div class="modal-footer">
							<div style="margin: 0;" class="form-actions">
								<a href="#" class="btn " data-dismiss="modal"><i18n:message key="citcorpore.comum.fechar" /></a> 
							</div>
						<!-- // Modal footer END -->
					</div>
				</div>
				
				<div class="modal hide fade in" id="modal_historicoCandidato" aria-hidden="false">
						<!-- Modal heading -->
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
							<h3></h3>
						</div>
						<!-- // Modal heading END -->
						<!-- Modal body -->
						<div class="modal-body">
							<form name='formHistoricoCandidato' action='<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/pages/historicoCandidato/historicoCandidato'>
		        					<input type='hidden' name='idSolicitacaoServico' id='idSolicitacaoSugestao'/>
		           				<div>
		           					<iframe width="99%" height="600" id='frameHistoricoCandidato' name='frameHistoricoCandidato' src=""></iframe>
		           				</div>
	    					</form>
						</div>
						<!-- // Modal body END -->
						<!-- Modal footer -->
						<div class="modal-footer">
							<div style="margin: 0;" class="form-actions">
								<a href="#" class="btn " data-dismiss="modal"><i18n:message key="citcorpore.comum.fechar" /></a> 
							</div>
						<!-- // Modal footer END -->
					</div>
				</div>
			
				</div>
				<!--  Fim conteudo-->
				
				 <%@include file="/novoLayout/common/include/libRodape.jsp" %> 
				<script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/ValidacaoUtils.js"></script>
    			<script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/PopupManager.js"></script>
   				<script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/cit/objects/RequisicaoPessoalDTO.js"></script>
   				<script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/cit/objects/TriagemRequisicaoPessoalDTO.js"></script>
   				<script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/cit/objects/CurriculoDTO.js"></script>
				
			</div>
	
	</body>
</html>