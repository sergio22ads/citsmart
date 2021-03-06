<%@page import="br.com.citframework.util.Constantes"%>
<%@ taglib uri="/tags/cit" prefix="cit"%>
<%@page import="br.com.citframework.util.UtilDatas"%>
<%@ taglib uri="/tags/i18n" prefix="i18n" %>
<%@page import="br.com.centralit.citcorpore.util.WebUtil"%>
<%@page import="br.com.centralit.citcorpore.bean.UsuarioDTO"%>
<!doctype html public "">
<html>
<head>
    <script type="text/javascript" src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/fckeditor/fckeditor.js"></script>
    <%
        response.setHeader("Cache-Control", "no-cache"); 
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        String id = request.getParameter("id");
    %>
    <%@include file="/include/internacionalizacao/internacionalizacao.jsp"%>
    <%@include file="/include/security/security.jsp" %>
    <title><i18n:message key="citcorpore.comum.title"/></title>
    <%@include file="/include/noCache/noCache.jsp" %>
   <%--  <%@include file="/include/menu/menuConfig.jsp" %>
    <%@include file="/include/header.jsp" %>
    <%@include file="/include/javaScriptsComuns/javaScriptsComuns.jsp" %>   
    <script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/ValidacaoUtils.js"></script>
    <script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/PopupManager.js"></script> --%>
    
    <%@include file="/novoLayout/common/include/libCabecalho.jsp" %>
		<link type="text/css" rel="stylesheet" href="../../novoLayout/common/include/css/template.css"/>  
	    <script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/ValidacaoUtils.js"></script>
	    <script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/js/PopupManager.js"></script>
	    <script type="text/javascript" src="<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/cit/objects/DescricaoCargoDTO.js"></script> 
	    <%@include file="/novoLayout/common/include/libRodape.jsp" %> 

    <style type="text/css">
        .destacado {
            border:1px solid #ddd;
            padding:4px 10px;
            border-left:none;
            background:#eee;
            font-size: 14px;
        }
        .table {
            border-left:1px solid #ddd;
             width: 90%;
        }
        
        .table th {
            border:1px solid #ddd;
            padding:4px 10px;
            border-left:none;
            background:#eee;
        }
        
        .table td {
            border:1px solid #ddd;
            padding:4px 10px;
            border-top:none;
            border-left:none;
        }
        
        .table1 {
        }
        
        .table1 th {
            border:1px solid #ddd;
            padding:4px 10px;
            background:#eee;
        }
        
        .table1 td {
        }   
             
         div#main_container {
            margin: 0px 0px 0px 0px;
        } 
                
        .container_16
        {
            width: 100%;
            margin: 0px 0px 0px 0px;
            
            letter-spacing: -4px;
        }
    </style>

    <script>
        $(function() {
            $("#POPUP_SELECAO").dialog({
                autoOpen : false,
                width : 500,
                height : 300,
                modal : true
            });
        }); 
        
        function limpar(){
    	  window.location = '<%=Constantes.getValue("SERVER_ADDRESS")%><%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/solicitacaoCargo/solicitacaoCargo.load';
        } 
    
        function desabilitarTela() {
            var f = document.form;
            for(i=0;i<f.length;i++){
                var el =  f.elements[i];
                if (el.type != 'hidden') { 
                    if (el.disabled != null && el.disabled != undefined) {
                        el.disabled = 'disabled';
                    }
                }
            }      
            document.getElementById('divAdicionarItem').style.display = 'none'; 
        }    

        addEvent(window, "load", load, false);
        function load(){        
            document.form.afterLoad = function () {
                parent.escondeJanelaAguarde();
            }    
        }

        function validar() {
            return document.form.validate();
        }

        function getObjetoSerializado() {
        	serializaObjetos('FormacaoAcademica','CargoFormacaoAcademicaDTO');
			serializaObjetos('Certificacao','CargoCertificacaoDTO');
			serializaObjetos('Curso','CargoCursoDTO');
			serializaObjetos('ExperienciaInformatica','CargoExperienciaInformaticaDTO');
			serializaObjetos('Idioma','CargoIdiomaDTO');
			serializaObjetos('Conhecimento','CargoConhecimentoDTO');
			serializaObjetos('ExperienciaAnterior','CargoExperienciaAnteriorDTO');
			serializaObjetos('Habilidade','CargoHabilidadeDTO');
			serializaObjetos('AtitudeIndividual','CargoAtitudeIndividualDTO');
		
            var obj = new CIT_DescricaoCargoDTO();
            HTMLUtils.setValuesObject(document.form, obj);
    		obj.serializeFormacaoAcademica = document.form.serializeFormacaoAcademica.value;
			obj.serializeCertificacao = document.form.serializeCertificacao.value;
			obj.serializeCurso = document.form.serializeCurso.value;
			obj.serializeExperienciaInformatica = document.form.serializeExperienciaInformatica.value;
			obj.serializeIdioma = document.form.serializeIdioma.value;
			obj.serializeExperienciaAnterior = document.form.serializeExperienciaAnterior.value;
			obj.serializeConhecimento = document.form.serializeConhecimento.value;
			obj.serializeHabilidade = document.form.serializeHabilidade.value;
			obj.serializeAtitudeIndividual = document.form.serializeAtitudeIndividual.value;
			
            return ObjectUtils.serializeObject(obj);
        }
        

	function select(id){
		document.form.idDescricaoCargo.value = id;
		document.form.fireEvent("restore");
	}    
	
	var contSelecao = 0;
	function exibirSelecao(objeto) {
		contSelecao = 0;
		HTMLUtils.deleteAllRows('tblSelecao');
		var nome = objeto.toUpperCase().substring(0,1) + objeto.substring(1,objeto.length);
		document.formSelecao.objeto.value = nome;
		document.formSelecao.action = '<%=Constantes.getValue("SERVER_ADDRESS")%><%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/' + objeto + '/' + objeto;
 		document.formSelecao.fireEvent('exibeSelecao');
		$("#POPUP_SELECAO").dialog("open");
	}
	
	function incluirOpcaoSelecao(id, desc, detalhe) {
		contSelecao ++;
		var tbl = document.getElementById('tblSelecao');
		var ultimaLinha = tbl.rows.length;
		var linha = tbl.insertRow(ultimaLinha);
		
		var coluna = linha.insertCell(0);
		coluna.className = "celulaEsquerda";
	    coluna.innerHTML = '<input style="cursor: pointer" type="checkbox" name="id" id="id' + contSelecao + '" value="'+id+'">';
	    
		coluna = linha.insertCell(1);
		coluna.className = "celulaEsquerda";
	    coluna.innerHTML = '<b>' +desc + '</b><input type="hidden" id="desc' + contSelecao + '" name="desc" value="' + desc + '" />';
	    
	    coluna = linha.insertCell(2);
		coluna.className = "celulaEsquerda";
		coluna.innerHTML = detalhe.substring(0,11) +'<span style="cursor: pointer;" title = "'+ detalhe +'"> ... </span> <input type="hidden" id="detalhe' + contSelecao + '" name="detalhe" value="' + detalhe + '" />';
	}
	
	function atribuirSelecao() {
		if (document.formSelecao.id.length) {
			var tbl = document.getElementById('tblSelecao');
			var ultimaLinha = tbl.rows.length;
            for(var i = 1; i < document.formSelecao.id.length; i++){
            	if (document.formSelecao.id[i].checked) {
            		var desc = document.getElementById('desc'+i).value;
					if (!validarLinhaSelecionada(document.formSelecao.objeto.value, ultimaLinha, document.formSelecao.id[i].value, desc))
						return;
				}
			}
            for(var i = 1; i < document.formSelecao.id.length; i++){
            	if (document.formSelecao.id[i].checked) {
            		var desc = document.getElementById('desc'+i).value;
            		var detalhe = document.getElementById('detalhe'+i).value;
            		adicionarLinhaSelecionada(document.formSelecao.objeto.value, document.formSelecao.id[i].value, desc, "N", detalhe);
				}
			}
		}
		$("#POPUP_SELECAO").dialog("close");
	}
	
	function inicializaContLinha() {
		document.getElementById('contFormacaoAcademica').value = '0';	
		document.getElementById('contCertificacao').value = '0';	
		document.getElementById('contCurso').value = '0';	
		document.getElementById('contExperienciaInformatica').value = '0';	
		document.getElementById('contIdioma').value = '0';	
		document.getElementById('contExperienciaAnterior').value = '0';
		document.getElementById('contConhecimento').value = '0';	
		document.getElementById('contHabilidade').value = '0';
		document.getElementById('contAtitudeIndividual').value = '0';
	}
	
	function adicionarLinhaSelecionada(objeto, id, desc, obrigatorio, detalhe){
		var contLinha = parseInt(document.getElementById('cont'+objeto).value);
		var checked = "";
		if (obrigatorio == "S")
			checked = "checked='true'";	
		contLinha ++;
		eval("document.getElementById('cont"+objeto+"').value = '"+contLinha+"'");
		var nomeTabela = 'tbl'+objeto;
		var tbl = document.getElementById(nomeTabela);
		tbl.style.display = 'block';
		var ultimaLinha = tbl.rows.length;
		var linha = tbl.insertRow(ultimaLinha);
		var coluna = linha.insertCell(0);
		coluna.className = "celulaEsquerda";
		coluna.innerHTML = '<img id="imgDel' + contLinha + '" style="cursor: pointer;" title="<i18n:message key="citcorpore.comum.cliquaParaExcluir"/>" src="<%=Constantes.getValue("SERVER_ADDRESS") + Constantes.getValue("CONTEXTO_APLICACAO")%>/imagens/delete.png" onclick="removerLinhaTabela(\''+nomeTabela+'\', this.parentNode.parentNode.rowIndex);">';
		coluna = linha.insertCell(1);
		coluna.className = "celulaEsquerda";
		coluna.innerHTML = desc + '<input type="hidden" id="id' + objeto + contLinha + '" name="id'+objeto+'" value="' + id + '" />';
		coluna = linha.insertCell(2);
		coluna.className = "celulaEsquerda";
		coluna.innerHTML = detalhe.substring(0,11) +'<span style="cursor: pointer;" title = "header=[<i18n:message key="analiseRequisicaoPessoal.detalheCompleto"/>] body=['+ detalhe +']"> ... </span>';
		coluna = linha.insertCell(3);
		coluna.className = "celulaCentralizada";
		coluna.innerHTML = '<input style="cursor: pointer" type="checkbox" id="obrig' + objeto + contLinha + '" name="obrig'+objeto+'" value="S" ' + checked + ' >';
	}
	
	function validarLinhaSelecionada(objeto, ultimaLinha, id, desc){
		if (ultimaLinha > 1){
			var arrayId = eval('document.form.id'+objeto);
			for (var i = 1; i < arrayId.length; i++){
				if (arrayId[i].value == id){
					alert(objeto + " ' " + desc + " ' " + i18n_message("rh.jaAdicionada"));
					return false;
				}
			}
		}
		return true;
	}
	
	function removerLinhaTabela(idTabela, rowIndex) {
		if (confirm(i18n_message("citcorpore.comum.deleta"))) {
			HTMLUtils.deleteRow(idTabela, rowIndex);
		}
	}
	
 	function serializaObjetos(objeto, tipo){
 		var tabela = document.getElementById('tbl'+objeto);
 		var lista = [];
		var arrayId = eval('document.form.id'+objeto);
		for (var i = 1; i < arrayId.length; i++){
 			var id = arrayId[i].value;
 			var chk = document.getElementById('obrig' + objeto + i);
 			var obrig = 'N';
 			if (chk.checked)
				obrig = 'S'; 				
 			var obj = eval('new '+tipo+'(' + id + ',"' + obrig + '")');
 			lista.push(obj);
 		} 	
 		var ser = ObjectUtils.serializeObjects(lista);
		document.getElementById('serialize'+objeto).value = ser;
 	}	

 	function CargoFormacaoAcademicaDTO(_id, _obrigatorio){
 		this.idFormacaoAcademica = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}

 	function CargoCertificacaoDTO(_id, _obrigatorio){
 		this.idCertificacao = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	
 	function CargoCursoDTO(_id, _obrigatorio){
 		this.idCurso = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	
 	function CargoExperienciaInformaticaDTO(_id, _obrigatorio){
 		this.idExperienciaInformatica = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	function CargoIdiomaDTO(_id, _obrigatorio){
 		this.idIdioma = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}

 	function CargoExperienciaAnteriorDTO(_id, _obrigatorio){
 		this.idConhecimento = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	
 	function CargoConhecimentoDTO(_id, _obrigatorio){
 		this.idConhecimento = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	
 	function CargoHabilidadeDTO(_id, _obrigatorio){
 		this.idHabilidade = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}
 	function CargoAtitudeIndividualDTO(_id, _obrigatorio){
 		this.idAtitudeIndividual = _id; 		
 		this.obrigatorio = _obrigatorio;
 	}

    </script>
</head>

<body>
		<div class="box grid_16 tabs">	 
    <div class="box grid_16 tabs" style='margin: 0px 0px 0px 0px;'>
        <div class="toggle_container">
            <div id="tabs-2" class="block" style="overflow: hidden;">
                        <form name='form' action='<%=CitCorporeConstantes.CAMINHO_SERVIDOR%><%=request.getContextPath()%>/pages/solicitacaoCargo/solicitacaoCargo'>
                                <input type='hidden' name='idDescricaoCargo'/>
                                <input type='hidden' name='idSolicitacaoServico'/>
                                <input type='hidden' name='situacao'/>
                                <input type='hidden' name='acao' id='acao'/> 

                                <input type='hidden' name='idFormacaoAcademica'/>
                                <input type='hidden' name='idCertificacao'/>
                                <input type='hidden' name='idCurso'/>
                                <input type='hidden' name='idExperienciaInformatica'/>
                                <input type='hidden' name='idExperienciaAnterior'/>
                                <input type='hidden' name='idIdioma'/>
                                <input type='hidden' name='idConhecimento'/>
                                <input type='hidden' name='idHabilidade'/>
                                <input type='hidden' name='idAtitudeIndividual'/>
                                
                                <input type='hidden' name='contFormacaoAcademica' id='contFormacaoAcademica' value='0'/>
                                <input type='hidden' name='contCertificacao' id='contCertificacao' value='0'/>
                                <input type='hidden' name='contCurso' id='contCurso' value='0'/>
                                <input type='hidden' name='contExperienciaInformatica' id='contExperienciaInformatica' value='0'/>
                                <input type='hidden' name='contExperienciaAnterior' id='contExperienciaAnterior' value='0'/>
                                <input type='hidden' name='contIdioma' id='contIdioma' value='0'/>
                                <input type='hidden' name='contConhecimento' id='contConhecimento' value='0'/>
                                <input type='hidden' name='contHabilidade' id='contHabilidade' value='0'/>
                                <input type='hidden' name='contAtitudeIndividual' id='contAtitudeIndividual' value='0'/>					                                

                                <input type='hidden' name='serializeFormacaoAcademica' id='serializeFormacaoAcademica'/>
                                <input type='hidden' name='serializeCertificacao' id='serializeCertificacao'/>
                                <input type='hidden' name='serializeCurso' id='serializeCurso'/>
                                <input type='hidden' name='serializeExperienciaInformatica' id='serializeExperienciaInformatica'/>
                                <input type='hidden' name='serializeIdioma' id='serializeIdioma'/>
                                <input type='hidden' name='serializeExperienciaAnterior' id='serializeExperienciaAnterior'/>
                                <input type='hidden' name='serializeConhecimento' id='serializeConhecimento'/>
                                <input type='hidden' name='serializeHabilidade' id='serializeHabilidade'/>
                                <input type='hidden' name='serializeAtitudeIndividual' id='serializeAtitudeIndividual'/>
                                
                                <div class="columns clearfix">
                                    <div class="col_100">				
									<fieldset>
										<label class="campoObrigatorio"><i18n:message key="cargo.cargo"/></label>
											<div>
												<input class="Valid[Required] Description[cargo.cargo]" type="text" onkeydown="FormatUtils.noNumbers(this)" onchange="FormatUtils.noNumbers(this)" name="nomeCargo" maxlength="80"/>
												
											</div>
									</fieldset>
								    </div>
								<div class="col_100">				
									<fieldset>
										<label class="campoObrigatorio"><i18n:message key="solicitacaoCargo.atividades"/></label>
											<div>
												<textarea class="Valid[Required] Description[solicitacaoCargo.atividades]" rows="5" cols="122" name='atividades' maxlength="100"></textarea>
											</div>
									</fieldset>
								</div>
								</div>
								
								
								
								<h2 id="perfilProfissional" class="section"><i18n:message key="solicitacaoCargo.perfilProfissional"/></h2>
								<div class="col_100">
									<div class="col_50">				
									<fieldset>
										<label class="campoObrigatorio"><i18n:message key="solicitacaoCargo.formacaoAcademica"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("formacaoAcademica")' style="cursor: pointer;" title="Clique para adicionar uma formação acadêmica"></label>
											<div  id="gridFormacaoAcademica">
												<table class="table" id="tblFormacaoAcademica" class="table" style="display: none;">
													<tr>
														<th style="width: 1%;font-size:10px; "></th>
														<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
														<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
														<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
													</tr>
												</table>
											</div>
									</fieldset>
									</div>
									<div class="col_50">
									<fieldset>
										<label ><i18n:message key="solicitacaoCargo.certificacoes"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("certificacao")' style="cursor: pointer;" title="Clique para adicionar uma certificação"></label>
										<div  id="gridCertificacao">
											<table class="table" id="tblCertificacao" class="table" style="display: none;">
												<tr>
													<th style="width: 1%;font-size:10px; "></th>
													<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
													<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
													<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
												</tr>
											</table>
										</div>
									</fieldset>
									</div>
								</div>	
								<div class="col_100">
								  <div class='col_50'>
								  <fieldset >
									<label><i18n:message key="solicitacaoCargo.cursos"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("curso")' style="cursor: pointer;" title="Clique para adicionar um curso"></label>
										<div  id="gridCurso">
											<table class="table" id="tblCurso" class="table" style="display: none;">
												<tr>
													<th style="width: 1%;font-size:10px; "></th>
													<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
													<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
													<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
												</tr>
											</table>
										</div>
								    </fieldset>
								  </div>
								  <div class='col_50'>
									  <fieldset >
											<label><i18n:message key="solicitacaoCargo.experienciaInformatica"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("experienciaInformatica")' style="cursor: pointer;" title="Clique para adicionar uma experência em informática"></label>
											<div  id="gridExperienciaInformatica">
												<table class="table" id="tblExperienciaInformatica" class="table" style="display: none;">
													<tr>
														<th style="width: 1%;font-size:10px; "></th>
														<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
													    <th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
													    <th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
													</tr>
												</table>
											</div>
									    </fieldset>
								  </div>
								</div>	
								<div class="col_100">
								  <div class='col_50'>
								  <fieldset >
									<label><i18n:message key="solicitacaoCargo.idiomas"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("idioma")' style="cursor: pointer;" title="Clique para adicionar um idioma"></label>
										<div  id="gridIdioma">
											<table class="table" id="tblIdioma" class="table" style="display: none;">
												<tr>
													<th style="width: 1%;font-size:10px; "></th>
													<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
													<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
													<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
												</tr>
											</table>
										</div>
								    </fieldset>
								  </div>
								  <div class='col_50'>
									  <fieldset >
											<label><i18n:message key="solicitacaoCargo.experienciaAnterior"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("experienciaAnterior")' style="cursor: pointer;" title="Clique para adicionar uma experiência anterior"></label>
											<div  id="gridExperienciaAnterior">
												<table class="table" id="tblExperienciaAnterior" class="table" style="display: none;">
													<tr>
														<th style="width: 1%;font-size:10px; "></th>
														<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
														<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
														<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
													</tr>
												</table>
											</div>
									    </fieldset>
								  </div>
								</div>	
								<div class="col_100">
								  <h2 id="perfilCompetencia" class="section"><i18n:message key="solicitacaoCargo.perfilCompetencia"/></h2>
								  </div>
								<div class="col_100">
								  <div class='col_50'>
									  <fieldset >
											<label class="campoObrigatorio"><i18n:message key="solicitacaoCargo.conhecimentos"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("conhecimento")' style="cursor: pointer;" title="Clique para adicionar um conhecimento"></label>
											<div  id="gridConhecimento">
												<table class="table" id="tblConhecimento" class="table" style="display: none;">
													<tr>
														<th style="width: 1%;font-size:10px; "></th>
														<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
														<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
														<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
													</tr>
												</table>
											</div>
									    </fieldset>
								  </div>
								  <div class='col_50'>
								  <fieldset >
									<label class="campoObrigatorio"><i18n:message key="solicitacaoCargo.habilidades"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("habilidade")' style="cursor: pointer;" title="Clique para adicionar uma habilidade"></label>
										<div  id="gridHabilidade">
											<table class="table" id="tblHabilidade" class="table" style="display: none;">
												<tr>
													<th style="width: 1%;font-size:10px; "></th>
													<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
													<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
													<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
												</tr>
											</table>
										</div>
								    </fieldset>
								  </div>
								</div>	  
								<div class="col_100">
									<div class='col_50'>
									  <fieldset >
											<label class="campoObrigatorio"><i18n:message key="solicitacaoCargo.atitudes"/><img src="<%=br.com.citframework.util.Constantes.getValue("CONTEXTO_APLICACAO")%>/template_new/images/icons/small/grey/magnifying_glass.png"  onclick='exibirSelecao("atitudeIndividual")' style="cursor: pointer;" title="Clique para adicionar uma atitude individual"></label>
											<div  id="gridAtitudeIndividual">
												<table class="table" id="tblAtitudeIndividual" class="table" style="display: none;">
													<tr>
														<th style="width: 1%;font-size:10px; "></th>
														<th style="font-size:10px;"><i18n:message key="citcorpore.comum.descricao"/></th>
														<th style="width: 40%;font-size:10px;"><i18n:message key="rh.detalhes"/></th>
														<th style="width: 10%;font-size:10px;"><i18n:message key="solicitacaoCaro.obrig"/></th>
													</tr>
												</table>
											</div>
									    </fieldset>
								  </div>
								 </div> 
								  <div class="col_100">
								  <h2 id="complementos" class="section"><i18n:message key="solicitacaoCargo.complementos"/></h2>
								  </div>

								  <div class="col_100">				
									<fieldset>
											<div>
												<textarea rows="5" cols="122" name='observacoes'></textarea>
											</div>
									</fieldset>
								</div>
                        </form>
                    </div>
            </div>  
        </div>
        
<div id="POPUP_SELECAO" title=<i18n:message key="rh.selecao"/> style="overflow: hidden">
   <form name='formSelecao' method="POST" action=''>
		<input type='hidden' name='id'/>
		<input type='hidden' name='sel'/>
		<input type='hidden' name='objeto' id='objetoSel'/>
		<div id='divSelecao' style='height:180px;overflow: auto;'>
			<table class="table" id="tblSelecao" class="table">
				<tr>
					<th style="width: 1%;">&nbsp;</th>
					<th ><i18n:message key="citcorpore.comum.descricao"/></th>
					<th style="width: 40%;">s</th><i18n:message key="rh.detalhes"/>
				</tr>
			</table>
		</div>
     	<table cellpadding="0" cellspacing="0">
          	<tr>
           		<td>
           		     &nbsp;
               		<input type='button' class="btn btn-primary" name='btnSelecionar' value='<i18n:message key='rh.selecionar'/>' onclick='atribuirSelecao()'/>
               		<input type='button' class="btn " name='btnFechar' value='Fechar' onclick='$("#POPUP_SELECAO").dialog("close");'/>
           		</td>
          	</tr>                                                                                      
      	</table>
   </form>
</div>
</div>
</body>

</html>
