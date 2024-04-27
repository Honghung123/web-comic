import React, { useState } from "react";
import Button from "@mui/material/Button";
import axios from "axios";

const comicTitle = "Chương 04: Thiên địa dị tượng, luyện thể thiên phú?";
const comicContent = `
<p>Hàng Châu tam trung trên bãi tập, các học sinh líu ríu.<br> <br> Ngay tại tổ chức, là Lam Tinh mỗi người đến mười tám tuổi liền muốn tiến hành thức tỉnh khảo thí.<br> 
<br> Mà lần này thiên phú thức tỉnh.<br> 
<br> "Ngọa tào! Lâm nữ thần thế mà đã thức tỉnh cấp S thiên phú!"<br> <br> "Tê! Ba chúng ta bên trong ra cấp S thiên phú!"<br> <br> Không sai.<br> <br> Lâm Ấu Vi thức tỉnh chính là vô cùng hi hữu cấp S thiên phú, hơn nữa còn là hệ chiến đấu!<br> <br> Không có gì bất ngờ xảy ra, tất nhiên là rất nhiều võ đạo đại học tranh đoạt đối tượng.<br> <br> Rất nhiều nguyên bản thầm mến Lâm Ấu Vi nam sinh, tại thời khắc này đều là nhịn không được ánh mắt ảm đạm xuống.<br> <br> Lâm gia thiên kim, lại phối hợp cái trước cấp S thiên phú.<br> <br> Bọn hắn chú định cùng Lâm Ấu Vi đã không thể nào là cùng người của một thế giới.<br> <br> "Lại là cấp S hệ chiến đấu!"<br> <br> Lâm Ấu Vi nhìn xem tự mình thiên phú, cũng rất là kinh ngạc, hưng phấn.<br> <br> Nàng nguyên bản đối với mình chờ mong, cũng chính là cái A.<br> <br> Dù sao cấp S loại này phi thường hi hữu tồn tại, cho dù là nàng Hàng Châu đệ nhất đại gia Lâm gia, cũng liền xuất hiện qua gia gia của nàng một vị mà thôi.<br> <br> Toàn bộ hàng thành phố hiện có cấp S, càng là một cánh tay đều có thể đếm rõ ràng, mà lại tuổi tác khoảng cách còn mười phần lớn.<br> <br> Đối với S, nàng thật sự là thật không dám muốn.<br> 
<br> Thì là Lâm Ấu Vi có hi vọng nhất siêu việt Lý Phàm một lần!<br> 
<br> "Vương Thần, cấp độ F sinh hoạt hệ, mỹ thực phẩm vị nhà "<br> <br> "Tôn Tử, cấp B hệ chiến đấu, cự phủ chi vương "<br> <br> . . .<br> <br> Thức tỉnh nghi thức bắt đầu.<br> <br> Các học sinh từng cái lên đài.<br> <br> Kinh hô tiếng thở dài liên tiếp.<br> <br> Có người vui vẻ có người sầu.<br> <br> Có thể nhìn thấy, nguyên bản còn cười đùa tí tửng học sinh, giờ phút này đều là sắc mặt nghiêm túc.<br> <br> Chơi thì chơi, nháo thì nháo, không cầm thiên phú nói đùa.<br> <br> Cái này thiên phú thức tỉnh xong sau, đời này có thể lẫn vào thế nào, thật là liền cơ bản giải quyết dứt khoát.<br> <br> "Vị kế tiếp, Lâm Ấu Vi!"<br> <br> Âm thanh âm vang lên.<br> <br> Lâm Ấu Vi một cái bước xa, không kịp chờ đợi vọt tới trên đài.<br>
<br> Bất quá sự thật liền bày ở trước mắt, cao hứng liền xong việc!<br> <br> Mà ngay sau đó, ánh mắt của nàng chính là nhìn về phía trong đám người Lý Phàm.<br> <br> Nàng cũng không tin.<br> <br> Lần này, sẽ còn không thắng được Lý Phàm!<br> <br> Lâm Ấu Vi nhìn chăm chú, đưa tới không ít các học sinh bạo động, nhao nhao cũng đều là nhìn về phía Lý Phàm.<br>  
<br> Ánh mắt mọi người tại lúc này cũng là thật chặt nhìn về phía Lâm Ấu Vi.<br> <br> Thân là trong trường vạn năm lão nhị.<br> <br> Lâm Ấu Vi thiên phú, quả thực mười phần để cho người ta chờ mong.<br> <br> Chỉ chốc lát sau.<br> <br> Theo tám mang tinh pháp trận sáng lên.<br> <br> Chỉ gặp một đạo lòe loẹt lóa mắt lam mang từ Lâm Ấu Vi trên thân nở rộ, thần thánh! Ưu nhã!<br> <br> Một giây sau.<br> <br> Lâm Ấu Vi kết quả rất nhanh liền ra lò.<br> <br> "Lâm Ấu Vi, cấp S hệ chiến đấu, băng nguyên tố thần tiễn thủ!"<br> <br> Nhìn xem kết quả ra một khắc.<br> <br> Toàn trường tất cả mọi người nhịn không được hít vào một ngụm khí lạnh.<br> <br> Trường học những người lãnh đạo càng là cả kinh trực tiếp đứng lên.<br> <br> "Cấp S thiên phú! Lại là cấp S!"<br> 
<br> "Ta nhìn khẩn trương là ngươi mới đúng."<br> <br> Lý Phàm đáp lễ Lâm Ấu Vi một cái mặt quỷ.<br> <br> Thiên phú cái đồ chơi này cũng không phải xem xuất thân, đừng tưởng rằng sinh ra ở Hàng Châu đệ nhất đại gia tộc Lâm gia, liền tuyệt đối có thể thức tỉnh tốt thiên phú.<br> <br> Cái đồ chơi này nhìn chính là huyền học!<br> <br> Lại nói, thật luận xuất thân, hắn nhưng là người xuyên việt, đơn giản kinh khủng như vậy!<br> <br> Cái này là người bình thường có thể so? !<br> <br> "Liễu Đào, cấp D hệ phụ trợ, dây leo buộc chặt "<br>
`;

export default function Home() {
  const handleSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      title: comicTitle,
      content: comicContent,
    };
    const url = `http://localhost:8080/api/v1/comic/export-file?converter_id=0`;
    try {
      const response = await axios.post(url, payload, {
        responseType: "blob",
      });
      console.log(response);
      const blob = new Blob([response.data], {
        type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      });
      const windowUrl = window.URL || window.webkitURL;
      const downloadUrl = windowUrl.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = downloadUrl;
      anchor.download = comicTitle;
      document.body.appendChild(anchor);
      anchor.click();
      // Xóa URL sau khi đã tải xuống
      window.URL.revokeObjectURL(downloadUrl);
    } catch (error) {
      alert(error);
    }
  };
  return (
    <>
      <h1 className="text-3xl font-bold underline">This is the Home</h1>

      <br />

      <Button variant="contained">Hello world</Button>
    </>
  );
}
