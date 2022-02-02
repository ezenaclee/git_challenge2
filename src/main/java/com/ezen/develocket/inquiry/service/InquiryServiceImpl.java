package com.ezen.develocket.inquiry.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ezen.develocket.inquiry.dao.InquiryDAO;
import com.ezen.develocket.inquiry.vo.InquiryImgVO;
import com.ezen.develocket.inquiry.vo.InquiryVO;


@Service("inquiryService")
@Transactional(propagation = Propagation.REQUIRED)
public class InquiryServiceImpl implements InquiryService {

	@Autowired
	private InquiryDAO inquiryDAO;

	@Override
	public Map<String, Object> inquiryList(Map<String, Integer> pagingMap) throws DataAccessException {

		List<InquiryVO> inquiryList = inquiryDAO.selectAllInquiryList(pagingMap);
		int totalInquiry = inquiryDAO.selectTotalInquiry();
		
		Map<String, Object> inquiryMap = new HashMap<>();
		inquiryMap.put("inquiryList", inquiryList);
		inquiryMap.put("totalInquiry", totalInquiry);
		
		return inquiryMap;
	}

	@Override
	public String addNewInquiry(Map<String, Object> inquiryMap) throws DataAccessException {

		String inquiry_cd = inquiryDAO.insertNewInquiry(inquiryMap);
		inquiryMap.put("inquiry_cd", inquiry_cd);
		
		inquiryDAO.insertNewImage(inquiryMap);
		
		return inquiry_cd;
	}

	@Override
	public Map<String, Object> viewInquiry(String inquiry_cd) throws DataAccessException {

		InquiryVO inquiryVO = inquiryDAO.selectInquiry(inquiry_cd);
		List<InquiryImgVO> imageFileList = inquiryDAO.selectImageFileList(inquiry_cd);
		
		Map<String, Object> inquiryMap = new HashMap<>();
		inquiryMap.put("inquiryVO", inquiryVO);
		inquiryMap.put("imageFileList", imageFileList);
		
		return inquiryMap;
	}

	@Override
	public void modifyInquiry(Map<String, Object> inquiryMap) throws DataAccessException {

		inquiryDAO.updateInquiry(inquiryMap);
		
		List<InquiryImgVO> imageFileList = (List<InquiryImgVO>) inquiryMap.get("imageFileList");
		List<InquiryImgVO> modAddImageFileList = (List<InquiryImgVO>) inquiryMap.get("modAddImageFileList");
		
		if (imageFileList != null && imageFileList.size() != 0) {
			int added_img_num = Integer.parseInt((String) inquiryMap.get("added_img_num"));
			int pre_img_num = Integer.parseInt((String) inquiryMap.get("pre_img_num"));
			
			if (pre_img_num < added_img_num) {	// 기존 이미지도 수정하고 새 이미지도 추가한 경우
				inquiryDAO.updateImageFile(inquiryMap);	// 기존 이미지 수정
				inquiryDAO.insertModNewImage(inquiryMap);	// 새 이미지 추가
			}
			else {	// 기존의 이미지만 수정한 경우
				inquiryDAO.updateImageFile(inquiryMap);
			}
		}
		// 새 이미지만 추가한 경우 (기존이미지 수정 안함)
		else if (modAddImageFileList != null && modAddImageFileList.size() != 0) {
			inquiryDAO.insertModNewImage(inquiryMap);	// 새 이미지 추가
		}
		
	}

	@Override
	public void removeInquiry(String inquiry_cd) throws DataAccessException {

		inquiryDAO.deleteInquiry(inquiry_cd);
	}

	@Override
	public void removeModImage(InquiryImgVO inquiryImgVO) throws DataAccessException {

		inquiryDAO.deleteModImage(inquiryImgVO);
	}
}
