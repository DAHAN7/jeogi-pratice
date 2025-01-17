package com.bitc.jeogi.review.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bitc.jeogi.common.util.Criteria;
import com.bitc.jeogi.common.util.PageMaker;
import com.bitc.jeogi.review.service.ReviewService;
import com.bitc.jeogi.review.vo.ReviewVO;
import com.bitc.jeogi.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("review/")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 페이징 처리 된 리뷰 목록 페이지
	 */
	@GetMapping("list")
	public void listPage(Criteria cri, Model model) throws Exception {
		log.info("리뷰 목록 페이지 요청됨");
		int totalCount = reviewService.getTotalCount();
		PageMaker pm = new PageMaker(cri, totalCount);
		List<ReviewVO> reviewList = reviewService.getPageReview(cri);
		
		model.addAttribute("reviews", reviewList);
		model.addAttribute("pm", pm);
	}

	@GetMapping("write")
	public void write() throws Exception {
		log.info("리뷰작성 페이지 요청됨");
	}

	/**
     * 리뷰 등록 요청 처리
     */
    @PostMapping("write")
    public String write(ReviewVO review, MultipartFile file,
            HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
    	  System.out.println("Review: " + review);
    	    System.out.println("File: " + file);
    	UserVO member = (UserVO) session.getAttribute("member");
        if (member == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            reviewService.write(review, file); 
            redirectAttributes.addFlashAttribute("msg", "리뷰가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "리뷰 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return "redirect:/review/list?accommodation_id=" + review.getAccommodation_id();
    }

    


	/**
	 * 리뷰 상세보기 페이지
	 */
	@GetMapping("detail")
	public void detail(int review_id, Model model) throws Exception {
		System.out.println("이다한");
		log.info("리뷰 상세보기 페이지 요청됨: review_id = {}", review_id);

		// 리뷰 상세 조회
		ReviewVO vo = reviewService.detail(review_id);
		model.addAttribute("review", vo);
	}

	/**
	 * 리뷰 수정 페이지 요청 - 리뷰 수정 화면 출력
	 */
	@GetMapping("update")
	public String update(int review_id, Model model, HttpSession s) throws Exception {
		
		ReviewVO vo = reviewService.findMemberId(review_id);
		UserVO member = (UserVO) s.getAttribute("member");
		log.info("다한"+member);
		if(member!=null) {
		if (vo.getU_id() == member.getU_no()) {
			log.info("리뷰 수정 페이지 요청됨: review_id = {}", review_id);
			ReviewVO review = reviewService.getById(review_id);
			model.addAttribute("review", review);
			return "review/update";
		} else {
			log.warn("로그인 불일치: review_id = {}, member_id = {}", review_id, member.getU_id());
			return "redirect:/review/detail?review_id="+review_id;
		}
		}else {
			return "redirect:/review/detail?review_id="+review_id;	
		}

	}

	/**
	 * 리뷰 수정 처리 요청
	 */
	@PostMapping("update")
	public String update(Model model, ReviewVO review, HttpSession s, RedirectAttributes rttr) throws Exception {
		log.info("리뷰 수정 요청됨: review = {}", review);
		String msg = reviewService.update(review);
		model.addAttribute("msg", msg);
		return "redirect:/review/list";

	}

//    @PostMapping("update")
//    public String update(ReviewVO vo, RedirectAttributes rttr) throws Exception {
//        log.info("리뷰 수정 요청됨: ReviewVO = {}", vo);
//
//        // 리뷰 수정 처리
//        String result = reviewService.update(vo);
//        rttr.addAttribute("review_id", vo.getReview_id());
//        rttr.addFlashAttribute("msg", result);
//        return "redirect:/review/detail";
//    }

	/**
	 * 리뷰 삭제 처리
	 */
	@PostMapping("delete")
	public String remove(int review_id, HttpSession s, RedirectAttributes rttr) throws Exception {
		log.info("리뷰 삭제 요청됨: review_id = {}", review_id);

		// 리뷰 삭제 처리
		UserVO loginMember = (UserVO) s.getAttribute("member");
		ReviewVO writer = reviewService.findMemberId(review_id);
		if (loginMember != null) {
			if (loginMember.getU_no() == writer.getU_id()) {

				String msg = reviewService.delete(review_id);
				rttr.addFlashAttribute("msg", msg);
				return "redirect:/review/list";

			} else {
				rttr.addFlashAttribute("msg", "글 작성자가 아니잔아요.");
				return "redirect:/review/list";
			}
		} else {
			rttr.addFlashAttribute("msg", "로그인안되어잇어요.");
			return "redirect:/review/list";
		}

	}
}
