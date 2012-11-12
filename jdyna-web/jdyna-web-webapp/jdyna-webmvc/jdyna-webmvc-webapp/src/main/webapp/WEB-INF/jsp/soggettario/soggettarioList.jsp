<%--
JDynA, Dynamic Metadata Management for Java Domain Object

 Copyright (c) 2008, CILEA and third-party contributors as
 indicated by the @author tags or express copyright attribution
 statements applied by the authors.  All third-party contributions are
 distributed under license by CILEA.

 This copyrighted material is made available to anyone wishing to use, modify,
 copy, or redistribute it subject to the terms and conditions of the GNU
 Lesser General Public License v3 or any later version, as published 
 by the Free Software Foundation, Inc. <http://fsf.org/>.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this distribution; if not, write to:
  Free Software Foundation, Inc.
  51 Franklin Street, Fifth Floor
  Boston, MA  02110-1301  USA
--%>

<%@ include file="/common/taglibs.jsp"%>
<%--VALIDATA XHTML 1.1 --%>
<script type="text/javascript">
    highlightTableRows("listaOggetti");
</script>


<head>
	<title>Lista dei Soggettari</title>
</head>

<c:if test="${!empty listaOggetti}">

<form:form action="export.htm" id="export" method="post">
<p>
	<img onclick="document.getElementById('export').submit()" src="${root}/images/ico_exp_xml.gif" title="<fmt:message key="esporta.configurazione.descrizione" />" alt="<fmt:message key="esporta.configurazione.descrizione" />"/>		
</p>
</form:form>

</c:if>

<div class="subcontenuto">
<display:table name="listaOggetti" cellspacing="0" cellpadding="0"
	requestURI="" id="listaOggetti" class="table" export="false" pagesize="50">
	<display:column property="id" escapeXml="true" sortable="true"
		titleKey="column.id" url="/soggettario/details.htm" paramId="id"
		paramProperty="id" />		
	<display:column property="nome" escapeXml="true" sortable="true"
		titleKey="column.nome"/>
	<display:column property="descrizione" escapeXml="true" sortable="true"
		titleKey="column.descrizione"/>

</display:table>



<c:set var="href">
	<c:url value="/flusso.flow?_flowId=nuovoSoggettario-flow"/>
</c:set>
<input type="button" value="<fmt:message key='button.create'/>"
	onclick="location.href='<c:out value='${href}' escapeXml='true' />'" />
</div>

<fieldset>
	<legend><fmt:message key="title.carica.configurazione" /></legend>
 		<p><fmt:message key="title.carica.configurazione.da.filesystem" /></p>
<form:form id="form2" action="import.htm" method="post" enctype="multipart/form-data">
<table>
		<tr>
			<td>
				File:
			</td>
			<td>
				<input type="file" name="file" size="80" />
			</td>
		</tr>
</table>		
<p>
		<input type="submit" name="upload"
		value="<fmt:message key='button.upload'/>"/>
</p>	 		 		
</form:form> 			
	
</fieldset>

