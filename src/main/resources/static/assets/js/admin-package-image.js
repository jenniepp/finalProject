function packageSubmit(){
    let image1 = $('#image1').val();
    let image2 = $('#image2').val();
    let postStart = $('#postStart').val();
    let postEnd = $('#postEnd').val();



    if(postStart>postEnd) {
        $("#postMessage").text("상품 게시기간을 확인해주세요.");
        return false;
    }
    else if(image1==""||image1==null) {
        $("#image1Message").text("상품 미리보기 이미지를 확인해주세요.");
        return false;
    }else if(image2==""||image2==null){
        $("#image2Message").text("상품 상세 이미지를 확인해주세요.");
        return false;
    }
}
