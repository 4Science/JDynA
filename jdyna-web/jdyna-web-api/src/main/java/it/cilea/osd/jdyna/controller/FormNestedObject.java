/*
 * JDynA, Dynamic Metadata Management for Java Domain Object
 * 
 *  Copyright (c) 2008, CILEA and third-party contributors as
 *  indicated by the @author tags or express copyright attribution
 *  statements applied by the authors.  All third-party contributions are
 *  distributed under license by CILEA.
 * 
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU
 *  Lesser General Public License v3 or any later version, as published 
 *  by the Free Software Foundation, Inc. <http://fsf.org/>.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 * 
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */
package it.cilea.osd.jdyna.controller;

import it.cilea.osd.common.controller.BaseFormController;
import it.cilea.osd.jdyna.dto.AnagraficaObjectAreaDTO;
import it.cilea.osd.jdyna.dto.AnagraficaObjectWithTypeDTO;
import it.cilea.osd.jdyna.dto.ValoreDTO;
import it.cilea.osd.jdyna.dto.ValoreDTOPropertyEditor;
import it.cilea.osd.jdyna.editor.FilePropertyEditor;
import it.cilea.osd.jdyna.model.ADecoratorTypeDefinition;
import it.cilea.osd.jdyna.model.ANestedObjectWithTypeSupport;
import it.cilea.osd.jdyna.model.ANestedPropertiesDefinition;
import it.cilea.osd.jdyna.model.ANestedProperty;
import it.cilea.osd.jdyna.model.ATypeNestedObject;
import it.cilea.osd.jdyna.model.AnagraficaSupport;
import it.cilea.osd.jdyna.model.PropertiesDefinition;
import it.cilea.osd.jdyna.model.Property;
import it.cilea.osd.jdyna.util.AnagraficaUtils;
import it.cilea.osd.jdyna.web.ITabService;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

public class FormNestedObject<SP extends Property<STP>, STP extends PropertiesDefinition, P extends ANestedProperty<PD>, PD extends ANestedPropertiesDefinition, TP extends ATypeNestedObject<PD>, ATN extends ADecoratorTypeDefinition<TP, PD>, ANO extends ANestedObjectWithTypeSupport<P, PD, SP, STP>> extends BaseFormController
{

    private ITabService applicationService;
    private Class<ANO> targetClass;
    private Class<TP> typeClass;
    private Class<PD> propertyDefinitionClass;
    private String specificPartPath;
    private String specificContextPath;
    
    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception
    {        
        String typeNestedStringID = request.getParameter("typeNestedID");
        Integer typeNestedID = Integer.parseInt(typeNestedStringID);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("simpleNameAnagraficaObject",
                targetClass.getSimpleName());
        TP typo = applicationService.get(typeClass, typeNestedID);
        model.put("maschera", typo.getMask());               
        String parentStringID = request.getParameter("parentID");
        String editmode = request.getParameter("editmode");
        String isAdmin = request.getParameter("admin");
                
        model.put("parentID", parentStringID);
        model.put("elementID", typo.getShortName());
        model.put("typeNestedID", typeNestedStringID);
        model.put("editmode", editmode);
        model.put("specificPartPath", getSpecificPartPath());
        model.put("specificContextPath", getSpecificContextPath());
        model.put("admin", isAdmin);
        return model;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception
    {
     
        String elementID = request.getParameter("elementID");        
        String parentStringID = request.getParameter("parentID");
        String typeNestedStringID = request.getParameter("typeNestedID");
                
        Integer parentID = Integer.parseInt(parentStringID);
        Integer typeNestedID = Integer.parseInt(typeNestedStringID);        
                
        TP typo = applicationService.get(typeClass, typeNestedID);
        ANO nested = targetClass.newInstance();
        if(elementID!=null && !elementID.isEmpty()) {
            String nestedID = elementID.substring(elementID.lastIndexOf("_")+1);            
            nested = applicationService.get(targetClass, Integer.parseInt(nestedID));            
        }
        nested.inizializza();
        
        
        AnagraficaObjectWithTypeDTO anagraficaObjectDTO = new AnagraficaObjectWithTypeDTO();        
        anagraficaObjectDTO.setTipologiaId(typo.getId());
        anagraficaObjectDTO.setParentId(parentID);
        anagraficaObjectDTO.setObjectId(nested.getId());
        
        
        AnagraficaUtils.fillDTO(anagraficaObjectDTO, nested, typo.getMask());
        return anagraficaObjectDTO;

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception
    {
        AnagraficaObjectWithTypeDTO anagraficaObjectDTO = (AnagraficaObjectWithTypeDTO) command;
        TP typo = applicationService.get(typeClass, anagraficaObjectDTO.getTipologiaId());
        
        
        ANO myObject = targetClass.newInstance();
        if(anagraficaObjectDTO.getObjectId()!=null) {
            myObject = applicationService.get(targetClass, anagraficaObjectDTO.getObjectId());          
        }
        
        boolean visible = AnagraficaUtils.checkIsVisible(anagraficaObjectDTO, typo.getMask());
        AnagraficaUtils.reverseDTO(anagraficaObjectDTO, myObject, typo.getMask());
        
        myObject.pulisciAnagrafica();
        myObject.setParent((AnagraficaSupport<SP, STP>)applicationService.get(myObject.getClassParent(), anagraficaObjectDTO.getParentId()));
        myObject.setTypo(typo);
        myObject.setStatus(visible);
                
        getApplicationService().saveOrUpdate(targetClass, myObject);
       
        Map<String, Object> model = new HashMap<String, Object>(); 
        Integer parentID = anagraficaObjectDTO.getParentId();
        Integer typeNestedID = anagraficaObjectDTO.getTipologiaId();
        
        List<ANO> results = applicationService.getNestedObjectsByParentIDAndTypoIDLimitAt(parentID, typeNestedID, targetClass, 5, 0);             
        
        long countAll = applicationService.countNestedObjectsByParentIDAndTypoID(parentID, typeNestedID, targetClass);        
        model.put("decoratorPropertyDefinition", applicationService.findContainableByDecorable(typo.getDecoratorClass(), typeNestedID));
        model.put("results", results);           
        model.put("offset", 5);
        model.put("limit", 5);
        model.put("pageCurrent", 0);   
        model.put("editmode", true);
        model.put("parentID", parentID);
        model.put("totalHit", (int)countAll);
        model.put("hitPageSize", results.size());
        model.put("specificPartPath", getSpecificPartPath());
        model.put("specificContextPath", getSpecificContextPath());
        model.put("admin", request.getAttribute("is.admin"));
        return new ModelAndView(getSuccessView(), model);// + anagraficaObjectDTO.getParentId() + "#viewnested_" + typo.getShortName());
    }

    
    @Override
    protected ServletRequestDataBinder createBinder(HttpServletRequest request,
            Object command) throws Exception
    {
        ServletRequestDataBinder servletRequestDataBinder = super.createBinder(
                request, command);
        AnagraficaObjectAreaDTO commandDTO = (AnagraficaObjectAreaDTO) command;
        for (String shortName : commandDTO.getAnagraficaProperties().keySet())
        {
            PD tipologiaProprieta = applicationService
                    .findPropertiesDefinitionByShortName(
                            propertyDefinitionClass, shortName);
            PropertyEditor propertyEditor = tipologiaProprieta.getRendering()
                    .getPropertyEditor(applicationService);
            if (propertyEditor instanceof FilePropertyEditor)
            {
                ((FilePropertyEditor) propertyEditor)
                        .setExternalAuthority(String.valueOf(commandDTO
                                .getParentId()));
                ((FilePropertyEditor) propertyEditor)
                        .setInternalAuthority(String.valueOf(tipologiaProprieta
                                .getId()));
            }

            String path = "anagraficaProperties[" + shortName + "]";
            servletRequestDataBinder.registerCustomEditor(ValoreDTO.class,
                    path, new ValoreDTOPropertyEditor(propertyEditor));
            // per le checkbox
            servletRequestDataBinder.registerCustomEditor(Object.class, path
                    + ".object", propertyEditor);
            logger.debug("Registrato Wrapper del property editor: "
                    + propertyEditor + " per il path: " + path);
            logger.debug("Registrato property editor: " + propertyEditor
                    + " per il path: " + path + ".object");

        }
        return servletRequestDataBinder;
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception
    {

        AnagraficaObjectWithTypeDTO dto = (AnagraficaObjectWithTypeDTO) command;
        TP typo = applicationService.get(typeClass, dto.getTipologiaId());
        
        
        ANO myObject = targetClass.newInstance();
        if(dto.getObjectId()!=null) {
            myObject = applicationService.get(targetClass, dto.getObjectId());          
        }
                
        
        AnagraficaUtils.reverseDTO(dto, myObject, typo.getMask());
        AnagraficaUtils.fillDTO(dto, myObject, typo.getMask());
    }
    

    public Class<ANO> getTargetClass()
    {
        return targetClass;
    }

    public void setTargetClass(Class<ANO> targetClass)
    {
        this.targetClass = targetClass;
    }

    public Class<TP> getTypeClass()
    {
        return typeClass;
    }

    public void setTypeClass(Class<TP> typeClass)
    {
        this.typeClass = typeClass;
    }

    public void setApplicationService(ITabService applicationService)
    {
        this.applicationService = applicationService;
    }

    public ITabService getApplicationService()
    {
        return applicationService;
    }

    public Class<PD> getPropertyDefinitionClass()
    {
        return propertyDefinitionClass;
    }

    public void setPropertyDefinitionClass(Class<PD> propertyDefinitionClass)
    {
        this.propertyDefinitionClass = propertyDefinitionClass;
    }

    public void setSpecificPartPath(String specificPartPath)
    {
        this.specificPartPath = specificPartPath;
    }

    public String getSpecificPartPath()
    {
        return specificPartPath;
    }

    public void setSpecificContextPath(String specificContextPath)
    {
        this.specificContextPath = specificContextPath;
    }

    public String getSpecificContextPath()
    {
        return specificContextPath;
    }

}
