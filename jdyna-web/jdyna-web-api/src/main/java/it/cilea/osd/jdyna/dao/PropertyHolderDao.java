/*
 * JDynA, Dynamic Metadata Management for Java Domain Object
 *
 * Copyright (c) 2008, CILEA and third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by CILEA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License v3 or any later version, as published 
 * by the Free Software Foundation, Inc. <http://fsf.org/>.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package it.cilea.osd.jdyna.dao;

import it.cilea.osd.common.dao.PaginableObjectDao;
import it.cilea.osd.jdyna.web.IContainable;
import it.cilea.osd.jdyna.web.IPropertyHolder;

import java.util.List;

public interface PropertyHolderDao<H extends IPropertyHolder> extends PaginableObjectDao<H,Integer> {
	public List<IContainable> findContainable(Integer boxID);
	public List<IContainable> findContainableOnCreation(Integer boxID);
	public List<H> findBoxByContainable(IContainable tip);
	public List<IContainable> findValoriOnCreation();
	public List<IContainable> findContainableWithRenderingFormula(Integer boxID);
}