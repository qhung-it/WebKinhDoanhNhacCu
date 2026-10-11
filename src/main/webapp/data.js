/* Local content retained for news and coupon displays. Catalog data comes from SQL Server. */

const COUPONS = {
  SOUNDORA10: { type: 'percent', value: 10, max: 500000, min: 0,       desc: 'Giảm 10% (tối đa 500.000đ)' },
  FREESHIP:   { type: 'ship',    value: 0,  max: 0,      min: 0,       desc: 'Miễn phí vận chuyển' },
  WELCOME50:  { type: 'amount',  value: 50000, max: 0,   min: 1000000, desc: 'Giảm 50.000đ cho đơn từ 1.000.000đ' }
};

const NEWS = [
  {
    id: 1, cat: 'Hướng dẫn', emoji: '🎸', color: '#6C5CE7', color2: '#A29BFE', date: '22/09/2026', author: 'Minh Hoàng',
    title: 'Hướng dẫn chọn Guitar Acoustic cho người mới bắt đầu',
    excerpt: 'Tìm hiểu các yếu tố quan trọng khi chọn mua cây đàn guitar acoustic đầu tiên: dáng đàn, loại gỗ mặt top, độ cao action và ngân sách phù hợp.',
    content: [
      'Chọn cây guitar đầu tiên là bước quan trọng quyết định bạn có giữ được niềm hứng thú khi tập hay không. Một cây đàn quá cứng tay hoặc phô ra âm thanh khó nghe sẽ khiến người mới nhanh nản.',
      'Hãy ưu tiên dáng đàn Dreadnought hoặc Concert tùy kích thước cơ thể, mặt top gỗ Spruce cho âm sáng, và action thấp (khoảng 2-3mm tại phím 12) để bấm dễ dàng. Ngân sách hợp lý cho người mới thường từ 2 đến 4 triệu đồng.',
      'Cuối cùng, đừng quên kiểm tra phụ kiện đi kèm như bao đàn, dây dự phòng, phím gảy và cờ lê chỉnh cần. Tại Soundora, đội ngũ tư vấn luôn sẵn sàng giúp bạn thử đàn trực tiếp trước khi quyết định.'
    ]
  },
  {
    id: 2, cat: 'Review', emoji: '🎹', color: '#0984E3', color2: '#74B9FF', date: '15/09/2026', author: 'Thanh Tâm',
    title: 'Review Piano điện Yamaha P-125: Đáng tiền trong tầm giá?',
    excerpt: 'Đánh giá chi tiết về cảm giác phím, chất lượng âm thanh, tính năng và độ bền của Yamaha P-125 sau hai tháng sử dụng thực tế.',
    content: [
      'Yamaha P-125 là một trong những cây piano điện bán chạy nhất thế giới nhờ sự cân bằng giữa chất lượng và giá thành. Thân máy gọn, loa tích hợp đủ lớn để tập luyện ở phòng nhỏ.',
      'Bàn phím GHS có độ nặng giảm dần từ trầm lên cao giống piano cơ, cho cảm giác chơi tự nhiên. Âm thanh được lấy mẫu từ grand piano concert nên khá đầy đặn, nhất là khi dùng tai nghe.',
      'Điểm trừ nhỏ là số lượng âm sắc không nhiều và không có Bluetooth. Tuy vậy, nếu bạn cần một cây đàn học nghiêm túc và bền bỉ thì P-125 vẫn là lựa chọn rất đáng cân nhắc.'
    ]
  },
  {
    id: 3, cat: 'Tin tức', emoji: '🎵', color: '#00B894', color2: '#55EFC4', date: '10/09/2026', author: 'Quốc Bảo',
    title: 'Soundora khai trương cửa hàng mới tại Quận 7 với nhiều ưu đãi',
    excerpt: 'Chào mừng cửa hàng thứ 5 trong hệ thống với không gian trải nghiệm nhạc cụ rộng hơn 300m2 và hàng loạt quà tặng cho khách hàng.',
    content: [
      'Nhằm phục vụ khách hàng khu vực phía Nam thành phố, Soundora chính thức khai trương cửa hàng mới tại Quận 7 với không gian rộng hơn 300m2, chia thành các khu trải nghiệm guitar, piano, trống và phụ kiện.',
      'Trong tuần khai trương, khách hàng sẽ được giảm đến 20% cho các dòng sản phẩm chọn lọc, tặng kèm bao đàn và khóa học thử miễn phí với giảng viên.',
      'Cửa hàng mở cửa từ 8:00 đến 21:00 tất cả các ngày trong tuần. Hãy ghé thử đàn và cảm nhận âm thanh trực tiếp bạn nhé.'
    ]
  },
  {
    id: 4, cat: 'Mẹo hay', emoji: '🎼', color: '#FDCB6E', color2: '#FFEAA7', date: '05/09/2026', author: 'Minh Hoàng',
    title: '7 mẹo bảo quản đàn guitar trong mùa mưa ẩm',
    excerpt: 'Khí hậu nồm ẩm có thể làm cong cần đàn, nở mặt gỗ và gỉ dây. Cùng xem những cách đơn giản để giữ đàn luôn ở trạng thái tốt nhất.',
    content: [
      'Độ ẩm cao là kẻ thù số một của nhạc cụ gỗ. Gỗ hút ẩm sẽ nở ra, làm thay đổi độ cong cần đàn, khiến action cao và gây ra tiếng rè.',
      'Hãy cất đàn trong bao hoặc hộp cứng kèm gói hút ẩm silica gel, tránh để sát tường hoặc gần cửa sổ. Thường xuyên lau dây bằng khăn mềm sau khi chơi và thay dây định kỳ 2-3 tháng.',
      'Nếu có điều kiện, hãy dùng máy hút ẩm hoặc điều hòa duy trì độ ẩm 40-55% trong phòng đặt đàn. Đây là cách bảo vệ cây đàn của bạn hiệu quả nhất.'
    ]
  },
  {
    id: 5, cat: 'Sự kiện', emoji: '🥁', color: '#FF6B6B', color2: '#FFA8A8', date: '28/08/2026', author: 'Thanh Tâm',
    title: 'Soundora Music Day 2026: Đêm nhạc cùng học viên và nghệ sĩ khách mời',
    excerpt: 'Sự kiện thường niên quy tụ hơn 500 người yêu nhạc với các tiết mục biểu diễn, workshop và nhiều phần quà hấp dẫn.',
    content: [
      'Soundora Music Day trở lại với chủ đề "Play Your Passion", nơi học viên, giảng viên và các nghệ sĩ khách mời cùng tham gia biểu diễn trên sân khấu ngoài trời.',
      'Bên cạnh phần biểu diễn, chương trình còn có các workshop ngắn về kỹ thuật fingerstyle, nhập môn trống điện và cách thu âm tại nhà với chi phí thấp.',
      'Vé tham dự miễn phí cho khách hàng đã mua sắm tại Soundora trong năm 2026. Đừng quên mang theo hóa đơn hoặc số điện thoại đã đăng ký khi tới sự kiện.'
    ]
  },
  {
    id: 6, cat: 'Hướng dẫn', emoji: '🎻', color: '#6C5CE7', color2: '#A29BFE', date: '20/08/2026', author: 'Quốc Bảo',
    title: 'Chọn Violin cho người mới: kích thước, chất liệu và phụ kiện',
    excerpt: 'Violin có nhiều kích cỡ từ 1/16 đến 4/4. Chọn đúng kích thước giúp bạn tránh đau vai, đau cổ khi tập và tiến bộ nhanh hơn.',
    content: [
      'Kích thước violin phụ thuộc vào chiều dài cánh tay của người chơi. Người lớn thường dùng size 4/4, trẻ em cần chọn size nhỏ hơn và thay đổi khi lớn lên.',
      'Mặt đàn nên bằng gỗ Spruce, lưng và cần đàn bằng gỗ Maple để có âm sáng. Đừng quên bộ phụ kiện như vĩ, nhựa thông, kê vai và hộp đàn.',
      'Bạn nên thử cầm đàn trước khi mua để cảm nhận độ vừa tay. Đội ngũ Soundora sẽ hỗ trợ đo kích thước và chỉnh dây miễn phí.'
    ]
  },
  {
    id: 7, cat: 'Review', emoji: '🔊', color: '#0984E3', color2: '#74B9FF', date: '12/08/2026', author: 'Thanh Tâm',
    title: 'Marshall MG15: Ampli nhỏ gọn cho người mới tập guitar điện',
    excerpt: 'Với công suất 15W và 2 kênh âm thanh, MG15 là lựa chọn đáng giá để tập luyện tại nhà mà vẫn giữ được chất rock.',
    content: [
      'Marshall MG15 nổi bật với thiết kế cổ điển và âm thanh đặc trưng của Marshall. Công suất 15W đủ để chơi trong phòng và tập cùng ban nhạc nhỏ.',
      'Kênh Clean cho âm thanh sạch và sáng, kênh Overdrive đủ gắt để chơi rock/blues. Núm chỉnh EQ 3 band dễ dùng, cổng tai nghe thuận tiện khi tập đêm.',
      'Tóm lại, đây là một chiếc ampli có giá hợp lý, bền bỉ và là bước khởi đầu tuyệt vời cho người mới chơi guitar điện.'
    ]
  },
  {
    id: 8, cat: 'Mẹo hay', emoji: '🎚️', color: '#FDCB6E', color2: '#FFEAA7', date: '02/08/2026', author: 'Minh Hoàng',
    title: 'Cách lên dây guitar chính xác chỉ trong 1 phút',
    excerpt: 'Chỉ cần một chiếc tuner kẹp hoặc ứng dụng điện thoại, bạn đã có thể lên dây guitar chuẩn mà không cần tai nghe nhạc.',
    content: [
      'Thứ tự dây guitar chuẩn từ dây 6 đến dây 1 là E - A - D - G - B - E. Bạn nên bắt đầu từ dây dày nhất và chỉnh từng dây một.',
      'Với tuner kẹp, hãy gảy từng dây nhẹ nhàng và vặn khóa cho đến khi kim hoặc màn hình báo xanh. Nếu dây bị thấp, hãy vặn lên từ từ để tránh đứt.',
      'Sau khi lên đủ 6 dây, hãy kiểm tra lại một lượt vì dây mới sẽ giãn và bị lệch. Thói quen này sẽ giúp tai nghe của bạn nhạy hơn qua thời gian.'
    ]
  }
];
