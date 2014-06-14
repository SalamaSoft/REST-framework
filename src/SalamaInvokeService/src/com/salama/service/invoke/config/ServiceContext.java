package com.salama.service.invoke.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author XingGu Liu
 *
 */
public class ServiceContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7123351151802347545L;

	private List<BasePackage> _dataScan = new ArrayList<BasePackage>();
	
	private List<BasePackage> _serviceScan = new ArrayList<BasePackage>();

	private ClassSetting _accessController = new ClassSetting();

	private UploadSetting _uploadSetting = new UploadSetting();
	
	private XmlResultSetting _xmlResultSetting = new XmlResultSetting();

	public ServiceContext() {
		//Default value
		_accessController.setClassName("com.salama.service.core.auth.AnnotationMethodAccessController");
	}
	
	public List<BasePackage> getDataScan() {
		return _dataScan;
	}

	public void setDataScan(List<BasePackage> dataScan) {
		_dataScan = dataScan;
	}

	public List<BasePackage> getServiceScan() {
		return _serviceScan;
	}

	public void setServiceScan(List<BasePackage> serviceScan) {
		_serviceScan = serviceScan;
	}

	public ClassSetting getAccessController() {
		return _accessController;
	}

	public void setAccessController(ClassSetting accessController) {
		_accessController = accessController;
	}

	public UploadSetting getUploadSetting() {
		return _uploadSetting;
	}

	public void setUploadSetting(UploadSetting uploadSetting) {
		_uploadSetting = uploadSetting;
	}

	public XmlResultSetting getXmlResultSetting() {
		return _xmlResultSetting;
	}

	public void setXmlResultSetting(XmlResultSetting xmlResultSetting) {
		_xmlResultSetting = xmlResultSetting;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
