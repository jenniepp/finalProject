package com.project.tour.service;


import com.project.tour.domain.Package;
import com.project.tour.domain.PackageDate;
import com.project.tour.repository.JejuPackageRepository;
import com.project.tour.repository.JejuSpecification;
import com.project.tour.repository.PackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PackageService {

    @Autowired
    private final PackageRepository packageRepository;

    @Autowired
    private final JejuPackageRepository jejuRepository;

    //페이징처리
    public Page<Package> getList(Pageable pageable) {

        List<Sort.Order> sorts = new ArrayList<Sort.Order>();
        sorts.add(Sort.Order.desc("id"));

        pageable = PageRequest.of(
                pageable.getPageNumber() <= 0 ? 0 :
                        pageable.getPageNumber() - 1,
                pageable.getPageSize(), Sort.by(sorts));

        return packageRepository.findAll(pageable);
    }
    public List<Package> getSearch(String keyword) {

        Specification<Package> spec = Specification.where(JejuSpecification.equalKeyword(keyword));

        return jejuRepository.findAll(spec);
    }

    public List<Package> getKeyword(List<String> keyword){


        return packageRepository.findByKeywordIn(keyword);
    }



    public Page<Package> getSearchList(String location, String date, Integer count, String keyword,
                                       List<String> transport, List<Integer> period, Integer pricerangestr, Integer pricerangeend ,
                                       Pageable pageable) {

        /** 날짜 */
        Specification<Package> spec = Specification.where(JejuSpecification.greaterThanOrEqualToDeparture(date));

        /** 상세지역 */
        if (location == null || location.equals("")) {
            location = "제주";
            spec = spec.and(JejuSpecification.equalLocation1(location));
        } else {
            spec = spec.and(JejuSpecification.equalLocation2(location));
        }

        /** 여행객수 */
        if (count != null) {
            spec = spec.and(JejuSpecification.greaterThanOrEqualToRemaincount(count));
        }

        /** 키워드 */
        if (keyword != null) {
            spec = spec.and(JejuSpecification.equalKeyword(keyword));
        }

        /** 항공사 */
        if(transport!=null){
            spec = spec.and(JejuSpecification.equalTransport(transport));
        }

        /** 여행기한 */
        if(period !=null){
            spec = spec.and(JejuSpecification.equalPeriod(period));
        }

        /** 가격범위 */
        if(pricerangestr !=null || pricerangeend !=null){
            spec = spec.and(JejuSpecification.betweenPrice(pricerangestr,pricerangeend));
        }

        List<Package> searchPackage = jejuRepository.findAll(spec);

        //중복제거
        Iterator<Package> it = searchPackage.iterator();
        HashSet<Long> packageNum = new HashSet<>();
        while (it.hasNext()) {
            packageNum.add(it.next().getId());
        }


        //페이징처리
        List<Sort.Order> sorts = new ArrayList<Sort.Order>();
        sorts.add(Sort.Order.desc("id"));

        pageable = PageRequest.of(
                pageable.getPageNumber() <= 0 ? 0 :
                        pageable.getPageNumber() - 1,
                pageable.getPageSize(), Sort.by(sorts));

        return packageRepository.findByIdIn(packageNum, pageable);

    }

    //데이터 불러오기 위한 임시
    private void create(Package apackage, PackageDate packageDate) {
        Package packages = new Package();
        PackageDate packageDates = new PackageDate();

        packages.setId(apackage.getId());
        packages.setLocation1(apackage.getLocation1());
        packages.setLocation2(apackage.getLocation2());
        packages.setPackageName(apackage.getPackageName());
        packages.setHotelName(apackage.getHotelName());
        packages.setPostStart(apackage.getPostStart());
        packages.setPostEnd(apackage.getPostEnd());
        packages.setCount(apackage.getCount());
        packages.setTravelPeriod(apackage.getTravelPeriod());
        packages.setPreviewImage(apackage.getPreviewImage());
        packages.setDetailImage(apackage.getDetailImage());
        packages.setPackageInfo(apackage.getPackageInfo());
        packages.setHitCount(apackage.getHitCount());
        packages.setKeyword(apackage.getKeyword());
        packages.setTransport(apackage.getTransport());
        packageDates.setDeparture(packageDate.getDeparture());
        packageDates.setDiscount(packageDate.getDiscount());
        packageDates.setAprice(packageDate.getAprice());
        packageDates.setBprice(packageDate.getBprice());
        packageDates.setCprice(packageDate.getCprice());
        packageDates.setRemaincount(packageDate.getRemaincount());

        packageRepository.save(packages);
    }


    //특정 packageNum으로 package data 출력(임시)
    public Package getPackage(long packageNum) {

        Optional<Package> packageData = packageRepository.findById(packageNum);

        return packageData.get();


    }

}
