package view;

import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.HoaDon;
import model.HoaDonChiTiet;
import model.KhachHang;
import model.NhanVien;
import model.SanPhamCT;
import repository.Authu;
import service.HoaDonCTService;
import service.HoaDonService;
import service.KhachHangService;
import service.NhanVienService;
import service.SanPhamCTService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import repository.Validated;

public class Form_BanHang extends javax.swing.JPanel implements Runnable, ThreadFactory {

    private final SanPhamCTService service = new SanPhamCTService();
    private final HoaDonService hdService = new HoaDonService();
    private final HoaDonCTService hoaDonCTService = new HoaDonCTService();
    private final NhanVienService nvService = new NhanVienService();
    private final KhachHangService khService = new KhachHangService();
    private int row = -1;
    private int rowSP = -1;
    private int rowCart = -1;
    private int pages = 1;
    private final int limit = 4;
    private int numberOfPages = 0;
    private int check;
    private int canExecute = 0;
    private WebcamPanel webcamPanel = null;
    private Webcam webcam = null;
    private volatile boolean isRunning = true;
    private final Executor executor = Executors.newSingleThreadExecutor(this);

    public Form_BanHang() {
        initComponents();
        this.loadSearch();
        this.fillTableSP();
        this.fillTableHD();
        String maNV = Authu.user.getMa();
        NhanVien nv = nvService.selectByMa(maNV);
        lblTenNV.setText(nv.getTen());
        if (txtSDT.getText() == null
                || txtSDT.getText().trim().isEmpty()) {
            lblTenKH.setText("Khác hàng chưa tồn tại");
        }
        this.loadTenKH();
        this.loadTienThua();
        initWebcam();
    }

    private void fillTableHD() {
        DefaultTableModel model = (DefaultTableModel) tblHoaDon.getModel();
        model.setRowCount(0);

        try {
            List<HoaDon> list = hdService.selectByStatus();
            for (int i = 0; i < list.size(); i++) {
                HoaDon hd = list.get(i);
                model.addRow(new Object[]{
                    i + 1,
                    hd.getMa(),
                    hd.getNgayTao(),
                    hd.getNv().getTen(),
                    hd.getTrangThai() ? "Đã thanh toán" : "Chờ thanh toán"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    private void setDataHoaDon(HoaDon hd) {
        lblMaHD.setText(hd.getMa());
        lblNgayMua.setText(String.valueOf(hd.getNgayTao()));
        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);
        List<HoaDonChiTiet> list = hoaDonCTService.selectByMaHD(hoaDon.getMa());
        Double tongTien = 0.0;
        for (int i = 0; i < list.size(); i++) {
            double giaTri = list.get(i).getTongTien();
            tongTien += giaTri;
        }
        lblTongTien.setText(String.valueOf(tongTien));
    }

    private HoaDon getDataBill() {
        HoaDon hd = new HoaDon();

        Date date = new Date();
        hd.setNgayTao(new java.sql.Date(date.getTime()));
        String maNV = Authu.user.getMa();
        NhanVien nv = nvService.selectByMa(maNV);
        hd.setIdNV(nv.getId());
        hd.setTongTien(null);
        hd.setIdKH(null);

        String status = "Chờ thanh toán";
        Boolean trangThai = status.equals("Đã thanh toán");
        hd.setTrangThai(trangThai);

        return hd;
    }

    private void insertBill() {
        check = JOptionPane.showConfirmDialog(this, "Bạn thực sự muốn tạo hóa đơn mới");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }
        HoaDon hoaDon = this.getDataBill();

        try {
            hdService.insert(hoaDon);
            this.fillTableHD();
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thất bại!");
        }
    }

    private void updateBill() {
        HoaDon hd = new HoaDon();

        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);

        String sdt = txtSDT.getText();
        KhachHang kh = khService.selectBySDT(sdt);
        hd.setIdKH(kh.getId());

        hd.setId(hoaDon.getId());

        hd.setTongTien(Double.parseDouble(lblTongTien.getText()));

        String status = "Đã thanh toán";
        Boolean trangThai = status.equals("Đã thanh toán");
        hd.setTrangThai(trangThai);

        try {
            hdService.update(hd);
            this.fillTableHD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Đổi trạng thái hóa đơn thất bại!");
        }
    }

    //End xử lý hóa đơn
    //Xử lý sản phẩm
    private void getPages(List<SanPhamCT> list) {
        if (list.size() % limit == 0) {
            numberOfPages = list.size() / limit;
        } else {
            numberOfPages = (list.size() / limit) + 1;
        }

        lblPages.setText("1");
    }

    private void fillTableSP() {
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        model.setRowCount(0);

        try {
            String keyWord = txtSearch.getText();

            List<SanPhamCT> listPage = service.selectPage(keyWord);
            this.getPages(listPage);

            List<SanPhamCT> list = service.selectStatus(keyWord, pages, limit);
            for (SanPhamCT spct : list) {
                model.addRow(new Object[]{
                    spct.getId(),
                    spct.getMaSP(),
                    spct.getSanPham().getTen(),
                    spct.getGia(),
                    spct.getSoLuong(),
                    spct.getSize().getTen(),
                    spct.getMauSac().getTen(),
                    spct.getChatLieu().getTen()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

//    private void fillCbbHinhDangFilter() {
//        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbFilterKieuDang.getModel();
//        model.removeAllElements();
//        model.addElement("");
//
//        List<HinhDang> listCbb = kdService.selectAll();
//        for (HinhDang hd : listCbb) {
//            model.addElement(hd.getTen());
//        }
//    }
//
//    private void fillCbbMauSacFilter() {
//        DefaultComboBoxModel model = (DefaultComboBoxModel) cbbFilterMau.getModel();
//        model.removeAllElements();
//        model.addElement("");
//
//        List<MauSac> listCbb = msService.selectAll();
//        for (MauSac mauSac : listCbb) {
//            model.addElement(mauSac.getTen());
//        }
//    }
    private void loadSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fillTableSP();
                firstPage();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fillTableSP();
                firstPage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fillTableSP();
                firstPage();
            }
        });
    }

    //Start filter---
    private void filter() {
//        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
//        model.setRowCount(0);
//
//        try {
//
//            Double giaMin = null;
//            if (!txtMin.getText().trim().isEmpty()) {
//                giaMin = Double.parseDouble(txtMin.getText());
//            }
//            Double giaMax = null;
//            if (!txtMax.getText().trim().isEmpty()) {
//                giaMax = Double.parseDouble(txtMax.getText());
//            }
//
//            String keyWord = txtSearch.getText();
//
//            String mau = (String) cbbFilterMau.getSelectedItem();
//
//            String hinhDang = (String) cbbFilterKieuDang.getSelectedItem();
//
//            List<SanPhamCT> listPage = spctService.FilterPage(keyWord, giaMin, giaMax, mau, hinhDang);
//            this.getPages(listPage);
//
//            List<SanPhamCT> list = spctService.FilterData(keyWord, giaMin, giaMax, mau, hinhDang, pages, limit);
//            for (SanPhamCT spct : list) {
//                model.addRow(new Object[]{
//                    spct.getId(),
//                    spct.getSanPham().getMa(),
//                    spct.getSanPham().getTen(),
//                    spct.getGia(),
//                    spct.getSoLuong(),
//                    spct.getMauSac().getTen(),
//                    spct.getHinhDang().getTen(),
//                    spct.loadTrangThai()
//                });
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
//        }
    }
//End filter---

//    private void updateStatusFilter() {
//        Boolean checkStatus = (canExecute == 1);
//        btnClean.setEnabled(checkStatus);
//    }
    //Start phân trang---
    private void firstPage() {
        pages = 1;
        if (canExecute == 1) {
            this.filter();
        } else {
            this.fillTableSP();
        }

        lblPages.setText("1");
    }

    private void prevPage() {
        if (pages > 1) {
            pages--;
            if (canExecute == 1) {
                this.filter();
            } else {
                this.fillTableSP();
            }

            lblPages.setText("" + pages);
        }
    }

    private void nextPage() {
        if (pages < numberOfPages) {
            pages++;
            if (canExecute == 1) {
                this.filter();
            } else {
                this.fillTableSP();
            }

            lblPages.setText("" + pages);
        }
    }

    private void lastPage() {
        pages = numberOfPages;
        if (canExecute == 1) {
            this.filter();
        } else {
            this.fillTableSP();
        }

        lblPages.setText("" + pages);
    }
//End phân trang---

    private SanPhamCT updateSoLuongSP(Integer soLuong) {
        SanPhamCT spct = new SanPhamCT();

        this.rowSP = tblSanPham.getSelectedRow();
        Integer idSP = (Integer) tblSanPham.getValueAt(rowSP, 0);
        SanPhamCT spctUpdate = service.selectById(idSP);

        Integer slMoi = spctUpdate.getSoLuong() - soLuong;

        String status;
        if (slMoi == 0) {
            status = "Ngừng bán";
        } else {
            status = "Đang bán";
        }
        Boolean trangThai = status.equals("Đang bán");
        spct.setTrangThai(trangThai);
        spct.setSoLuong(slMoi);

        spct.setId(spctUpdate.getId());

        return spct;
    }

    private void updateDataProducts(SanPhamCT spct) {
        try {
            service.updateSoLuong(spct);
            this.fillTableSP();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update số lượng thất bại!");
        }
    }
//End Xử lý sản phẩm

    //Xử lý giỏ hàng
    private void fillTableGioHang(HoaDon hoaDon) {
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        model.setRowCount(0);

        try {
            List<HoaDonChiTiet> list = hoaDonCTService.selectByMaHD(hoaDon.getMa());
            for (HoaDonChiTiet hdct : list) {
                model.addRow(new Object[]{
                    hdct.getId(),
                    hdct.getSpct().getSanPham().getMa(),
                    hdct.getSpct().getSanPham().getTen(),
                    hdct.getSpct().getSize().getTen(),
                    hdct.getSpct().getMauSac().getTen(),
                    hdct.getSpct().getChatLieu().getTen(),
                    hdct.getGia(),
                    hdct.getSoLuong(),
                    hdct.getTongTien()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    private HoaDonChiTiet getDataCart(Integer soLuong) {
        HoaDonChiTiet hdct = new HoaDonChiTiet();

        this.rowSP = tblSanPham.getSelectedRow();
        Integer idSP = (Integer) tblSanPham.getValueAt(rowSP, 0);
        SanPhamCT spct = service.selectById(idSP);

        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);

        Double gia = spct.getGia();
        hdct.setGia(gia);
        hdct.setSoLuong(soLuong);
        hdct.setTongTien(gia * soLuong);
        hdct.setIdSP(spct.getId());
        hdct.setIdHD(hoaDon.getId());

        return hdct;
    }

    private void insertCart(HoaDonChiTiet hdct) {
        try {
            hoaDonCTService.insert(hdct);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thêm vào giỏ hàng thất bại!");
        }
    }

    private void updateCart(Integer soLuong) {
        this.rowSP = tblSanPham.getSelectedRow();
        Integer idSP = (Integer) tblSanPham.getValueAt(rowSP, 0);

        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);
        List<HoaDonChiTiet> list = hoaDonCTService.selectByMaHD(hoaDon.getMa());

        HoaDonChiTiet hdct = new HoaDonChiTiet();
        hdct.setSoLuong(soLuong);

        for (HoaDonChiTiet hoaDonChiTiet : list) {
            if (Objects.equals(idSP, hoaDonChiTiet.getIdSP())) {
                hdct.setId(hoaDonChiTiet.getId());
                break;
            }
        }

        try {
            hoaDonCTService.update(hdct);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thêm vào giỏ hàng thất bại!");
        }
    }

    private void updateCartAndProducr(Integer newQuantity) {
        HoaDonChiTiet hdct = new HoaDonChiTiet();

        this.rowCart = tblGioHang.getSelectedRow();
        Integer idHdct = (Integer) tblGioHang.getValueAt(rowCart, 0);
        HoaDonChiTiet hdctBanDau = hoaDonCTService.selectById(idHdct);

        SanPhamCT spct = new SanPhamCT();
        SanPhamCT spctUpdate = service.selectById(hdctBanDau.getIdSP());

        Integer checkSL = newQuantity;
        try {
            if (newQuantity >= hdctBanDau.getSoLuong()) {
                if (spctUpdate.getSoLuong() == 0) {
                    checkSL = hdctBanDau.getSoLuong();
                }
            } else {
                checkSL = newQuantity;
            }
            hdct.setSoLuong(checkSL);
            hdct.setId(hdctBanDau.getId());
            if (newQuantity == 0) {
                hoaDonCTService.delete(idHdct);
            } else {
                hoaDonCTService.update(hdct);
            }

            this.row = tblHoaDon.getSelectedRow();
            String maHD = (String) tblHoaDon.getValueAt(row, 1);
            HoaDon hoaDon = hdService.selectByMa(maHD);
            this.fillTableGioHang(hoaDon);

            Integer slThayDoi = checkSL - hdctBanDau.getSoLuong();
            Integer slMoi = spctUpdate.getSoLuong() - slThayDoi;

            String status;
            if (slMoi > 0) {
                status = "Đang bán";
            } else {
                status = "Ngừng bán";
            }

            Boolean trangThai = status.equals("Đang bán");
            spct.setTrangThai(trangThai);
            spct.setSoLuong(slMoi);
            spct.setId(spctUpdate.getId());
            this.updateDataProducts(spct);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Update số lượng cart thất bại!");
        }
    }
//END xử lý giỏ hàng

    //Quét qr
    private void initWebcam() {
        Dimension size = new Dimension(176, 144);

        webcam = Webcam.getWebcams().get(0);

        closeWebcam();

        webcam.setViewSize(size);

        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setPreferredSize(size);
        webcamPanel.setFPSDisplayed(true);

        pnlCam.setLayout(new BorderLayout());
        pnlCam.add(webcamPanel, BorderLayout.CENTER);

        executor.execute(this);
    }

    private void closeWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(com.google.zxing.qrcode.encoder.QRCode.class.getName()).log(Level.SEVERE, null, ex);
            }

            Result result = null;
            BufferedImage image = null;

            // Đảm bảo rằng webcam đã mở và có hình ảnh trước khi xử lý
            if (webcam != null && webcam.isOpen() && (image = webcam.getImage()) != null) {
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                SwingUtilities.invokeLater(() -> webcamPanel.repaint());

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException ex) {
                    Logger.getLogger(com.google.zxing.qrcode.encoder.QRCode.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (result != null) {
                    Integer idSP = Integer.parseInt(result.getText());
                    SanPhamCT sanPhamCT = service.selectById(idSP);

                    if (sanPhamCT == null) {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với ID: " + idSP);
                        continue;
                    }

                    String input = JOptionPane.showInputDialog(this, "Nhập số lượng:");
                    if (input == null || input.isEmpty()) {
                        continue;
                    }
                    Integer soLuong = Integer.parseInt(input);
                    if (soLuong < 0) {
                        JOptionPane.showMessageDialog(this, "Số lượng phải > 0");
                        continue;
                    }

                    Integer slsp = sanPhamCT.getSoLuong();

                    if (soLuong > slsp) {
                        JOptionPane.showMessageDialog(this, "Sản phẩm chỉ còn lại" + slsp);
                        soLuong = slsp;
                    }

                    Integer soLuongSp = 0;
                    this.row = tblHoaDon.getSelectedRow();
                    if (row < 0) {
                        JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để thêm sản phẩm!");
                        continue;
                    }
                    String maHD = (String) tblHoaDon.getValueAt(row, 1);
                    HoaDon hoaDon = hdService.selectByMa(maHD);
                    List<HoaDonChiTiet> list = hoaDonCTService.selectByMaHD(hoaDon.getMa());

                    for (HoaDonChiTiet hoaDonChiTiet : list) {
                        if (Objects.equals(idSP, hoaDonChiTiet.getIdSP())) {
                            soLuongSp = hoaDonChiTiet.getSoLuong() + soLuong;
                            HoaDonChiTiet hdct = new HoaDonChiTiet();
                            hdct.setSoLuong(soLuongSp);
                            hdct.setId(hoaDonChiTiet.getId());

                            try {
                                hoaDonCTService.update(hdct);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(this, "Thêm vào giỏ hàng thất bại!");
                            }

                            break;
                        }
                    }

                    if (soLuongSp == 0) {
                        soLuongSp = soLuong;

                        HoaDonChiTiet hdct = new HoaDonChiTiet();
                        Double gia = sanPhamCT.getGia();
                        hdct.setGia(gia);
                        hdct.setSoLuong(soLuongSp);
                        hdct.setIdSP(sanPhamCT.getId());
                        hdct.setIdHD(hoaDon.getId());

                        this.insertCart(hdct);
                    }

                    //Load table giỏ hàng
                    this.fillTableGioHang(hoaDon);
                    this.setDataHoaDon(hoaDon);

                    SanPhamCT spct = new SanPhamCT();
                    SanPhamCT spctUpdate = service.selectById(idSP);

                    Integer slMoi = spctUpdate.getSoLuong() - soLuong;
                    spct.setSoLuong(slMoi);
                    String status;
                    if (slMoi == 0) {
                        status = "Đang bán";
                    } else {
                        status = "Ngừng bán";
                    }

                    Boolean trangThai = status.equals("Đang bán");
                    spct.setTrangThai(trangThai);
                    spct.setId(spctUpdate.getId());
                    this.updateDataProducts(spct);
                }
            }
        } while (isRunning);
    }

    public void stopThread() {
        isRunning = false;
        closeWebcam();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "my Thread");
        t.setDaemon(true);
        return t;
    }
    //End quét qr

    //Xử lý thanh toán
    private void fillTenKH() {
        String sdt = txtSDT.getText().trim();

        if (sdt == null || sdt.isEmpty()) {
            lblTenKH.setText("Khách hàng chưa tồn tại");
            return;
        }

        List<KhachHang> list = khService.selectAll();
        boolean found = false;

        for (KhachHang kh : list) {
            if (sdt.equals(kh.getSdt())) {
                lblTenKH.setText(kh.getTen());
                found = true;
                break;
            }
        }

        if (!found) {
            lblTenKH.setText("Khách hàng chưa tồn tại");
        }
    }

    private void loadTenKH() {
        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fillTenKH();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fillTenKH();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fillTenKH();
            }
        });
    }

    public void fillTienThua() {
        if (!Validated.isNumericDouble(txtTienTra.getText())) {
            return;
        }
        Double tienTra = Double.parseDouble(txtTienTra.getText());
        Double tongTien = Double.parseDouble(lblTongTien.getText());
        Double tienThua = tongTien - tienTra;
        if (tienTra <= tongTien) {
            tienThua = 0.0;
        } else {
            tienThua = -tienThua;
        }
        lblTienThua.setText(String.valueOf(tienThua));
    }

    private void loadTienThua() {
        txtTienTra.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fillTienThua();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fillTienThua();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fillTienThua();
            }
        });
    }

    private void ThanhToan() {
        if (lblMaHD.getText() == null || lblMaHD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để thanh toán!");
            return;
        }

        if (lblTongTien.getText() == null || lblTongTien.getText().trim().isEmpty() || Double.parseDouble(lblTongTien.getText()) == 0.0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để thanh toán!");
            return;
        }

        boolean khachHangTonTai = false;
        List<KhachHang> list = khService.selectAll();
        for (KhachHang khachHang : list) {
            if (txtSDT.getText() != null && !txtSDT.getText().trim().isEmpty() && txtSDT.getText().trim().equals(khachHang.getSdt())) {
                khachHangTonTai = true;
                break;
            }
        }

        if (!khachHangTonTai) {
            JOptionPane.showMessageDialog(this, "Khách hàng không tồn tại");
            return;
        }

        if (txtSDT.getText() == null || txtSDT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Khách hàng không tồn tại");
            return;
        }

        if (txtTienTra.getText() == null || txtTienTra.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập vào tiền trả!");
            return;
        }

        if (Double.parseDouble(lblTongTien.getText()) > Double.parseDouble(txtTienTra.getText())) {
            JOptionPane.showMessageDialog(this, "Vui lòng trả đủ tiền để thanh toán!");
            return;
        }

        this.updateBill();

        lblMaHD.setText("");
        txtSDT.setText("");
        lblNgayMua.setText("");
        lblTongTien.setText("");
        txtTienTra.setText("");
        lblTienThua.setText("");
        DefaultTableModel model = (DefaultTableModel) tblGioHang.getModel();
        model.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField4 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        btnTaoHD = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblGioHang = new javax.swing.JTable();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSanPham = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnFirst_Product = new javax.swing.JButton();
        btnPrev_Product = new javax.swing.JButton();
        lblPages = new javax.swing.JLabel();
        btnNext_Product = new javax.swing.JButton();
        btnLast_Product = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblNgayMua = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblTenNV = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtTienTra = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lblTienThua = new javax.swing.JLabel();
        btnThanhToan = new javax.swing.JButton();
        lblMaHD = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        lblTenKH = new javax.swing.JLabel();
        btnKhachHang = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        pnlCam = new javax.swing.JPanel();

        jTextField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        setPreferredSize(new java.awt.Dimension(1000, 700));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Quản Lý Bán Hàng");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã HĐ", "Ngày Tạo", "Tên NV", "Trang Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHoaDon);

        btnTaoHD.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnTaoHD.setText("Tạo Hóa Đơn");
        btnTaoHD.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTaoHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaoHDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnTaoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnTaoHD, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 106, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Giỏ Hàng");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblGioHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã SP", "Tên SP", "Size", "màu sắc", "Chất liệu", "Giá", "Số lượng", "Tổng tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGioHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblGioHangMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblGioHang);

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSua)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnSua))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Sản Phẩm");

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã SP", "Tên SP", "Giá Bán", "Số Lượng SP", "Size", "Màu sắc", "Chất liệu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSanPhamMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblSanPham);

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setText("Search");

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnFirst_Product.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnFirst_Product.setText("<<");
        btnFirst_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirst_ProductActionPerformed(evt);
            }
        });

        btnPrev_Product.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnPrev_Product.setText("<");
        btnPrev_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrev_ProductActionPerformed(evt);
            }
        });

        lblPages.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        btnNext_Product.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnNext_Product.setText(">");
        btnNext_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNext_ProductActionPerformed(evt);
            }
        });

        btnLast_Product.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnLast_Product.setText(">>");
        btnLast_Product.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLast_ProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFirst_Product)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrev_Product)
                .addGap(10, 10, 10)
                .addComponent(lblPages, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNext_Product)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLast_Product)
                .addGap(360, 360, 360))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFirst_Product)
                        .addComponent(btnPrev_Product))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNext_Product)
                        .addComponent(btnLast_Product))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPages, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Mã HĐ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Ngày mua");

        lblNgayMua.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setText("Tên NV");

        lblTenNV.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setText("Tổng tiền");

        lblTongTien.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setText("Tiền đã trả");

        txtTienTra.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setText("Tiền thừa");

        lblTienThua.setBackground(new java.awt.Color(255, 255, 255));

        btnThanhToan.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        lblMaHD.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setText("SDT");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Tên KH");

        btnKhachHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/adds.png"))); // NOI18N
        btnKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhachHangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNgayMua, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTenNV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTongTien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTienThua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTienTra)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMaHD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTenKH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(lblMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblTenKH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNgayMua, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(lblTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtTienTra, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(lblTienThua, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Hóa Đơn");

        pnlCam.setBackground(new java.awt.Color(255, 255, 255));
        pnlCam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlCamLayout = new javax.swing.GroupLayout(pnlCam);
        pnlCam.setLayout(pnlCamLayout);
        pnlCamLayout.setHorizontalGroup(
            pnlCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 174, Short.MAX_VALUE)
        );
        pnlCamLayout.setVerticalGroup(
            pnlCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 142, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(204, 204, 204))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(118, 118, 118))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(pnlCam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(4, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlCam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)))
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnFirst_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirst_ProductActionPerformed
        // TODO add your handling code here:
        this.firstPage();
    }//GEN-LAST:event_btnFirst_ProductActionPerformed

    private void btnTaoHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaoHDActionPerformed
        // TODO add your handling code here:
        this.insertBill();
    }//GEN-LAST:event_btnTaoHDActionPerformed

    private void tblHoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonMouseClicked
        // TODO add your handling code here:
        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);
        this.fillTableGioHang(hoaDon);
        this.setDataHoaDon(hoaDon);
    }//GEN-LAST:event_tblHoaDonMouseClicked

    private void tblSanPhamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSanPhamMouseClicked
        if (evt.getClickCount() == 2) {

            String input = JOptionPane.showInputDialog(this, "Nhập số lượng:");
            if (input == null || input.isEmpty()) {
                return;
            }
            Integer soLuong = Integer.parseInt(input);
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải > 0");
                return;
            }

            this.rowSP = tblSanPham.getSelectedRow();
            Integer slsp = (Integer) tblSanPham.getValueAt(rowSP, 4);
            if (soLuong > slsp) {
                JOptionPane.showMessageDialog(this, "Sản phẩm chỉ còn lại " + slsp);
                soLuong = slsp;
            }

            //Thêm sp vào giỏ hàng
            Integer soLuongSp = 0;
            this.row = tblHoaDon.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để thêm sản phẩm!");
                return;
            }
            String maHD = (String) tblHoaDon.getValueAt(row, 1);
            HoaDon hoaDon = hdService.selectByMa(maHD);
            List<HoaDonChiTiet> list = hoaDonCTService.selectByMaHD(hoaDon.getMa());

            Integer idSP = (Integer) tblSanPham.getValueAt(rowSP, 0);

            for (HoaDonChiTiet hoaDonChiTiet : list) {
                if (Objects.equals(idSP, hoaDonChiTiet.getIdSP())) {
                    soLuongSp = hoaDonChiTiet.getSoLuong() + soLuong;
                    this.updateCart(soLuongSp);
                    break;
                }
            }

            if (soLuongSp == 0) {
                soLuongSp = soLuong;

                HoaDonChiTiet hdct = this.getDataCart(soLuongSp);
                this.insertCart(hdct);
            }
            //Load table giỏ hàng
            this.fillTableGioHang(hoaDon);
            this.setDataHoaDon(hoaDon);

            //update lại số lượng sản phẩm
            SanPhamCT spctUpdate = this.updateSoLuongSP(soLuong);
            this.updateDataProducts(spctUpdate);
        }
    }//GEN-LAST:event_tblSanPhamMouseClicked

    private void tblGioHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblGioHangMouseClicked

    }//GEN-LAST:event_tblGioHangMouseClicked

    private void btnPrev_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrev_ProductActionPerformed
        // TODO add your handling code here:
        this.prevPage();
    }//GEN-LAST:event_btnPrev_ProductActionPerformed

    private void btnNext_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNext_ProductActionPerformed
        // TODO add your handling code here:
        this.nextPage();
    }//GEN-LAST:event_btnNext_ProductActionPerformed

    private void btnLast_ProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLast_ProductActionPerformed
        // TODO add your handling code here:
        this.lastPage();
    }//GEN-LAST:event_btnLast_ProductActionPerformed

    private void btnKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhachHangActionPerformed
        // TODO add your handling code here:
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            KhachHangJDialog khachHangDialog = new KhachHangJDialog(frame, true);
            khachHangDialog.setVisible(true);
        }
        
        List<KhachHang> list = khService.selectAll();
        if (!list.isEmpty()) {
            KhachHang lastKhachHang = list.get(list.size() - 1);
            txtSDT.setText(lastKhachHang.getSdt());
        }
    }//GEN-LAST:event_btnKhachHangActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        // TODO add your handling code here:
        this.ThanhToan();
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        this.rowCart = tblGioHang.getSelectedRow();
        if (rowCart < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trong giỏ hàng!");
            return;
        }

        Integer idHDCT = (Integer) tblGioHang.getValueAt(rowCart, 0);
        HoaDonChiTiet hdctBanDau = hoaDonCTService.selectById(idHDCT);
        SanPhamCT spctUpdate = service.selectById(hdctBanDau.getIdSP());

        SanPhamCT spct = new SanPhamCT();
        try {
            hoaDonCTService.delete(idHDCT);
            this.row = tblHoaDon.getSelectedRow();
            String maHD = (String) tblHoaDon.getValueAt(row, 1);
            HoaDon hoaDon = hdService.selectByMa(maHD);
            this.fillTableGioHang(hoaDon);

            Integer slMoi = spctUpdate.getSoLuong() + hdctBanDau.getSoLuong();
            spct.setSoLuong(slMoi);

            String status = "Đang bán";
            Boolean trangThai = status.equals("Đang bán");
            spct.setTrangThai(trangThai);
            spct.setId(spctUpdate.getId());
            this.updateDataProducts(spct);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Xóa thất bại!");
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
        this.rowCart = tblGioHang.getSelectedRow();
        if (rowCart < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trong giỏ hàng để sửa!");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Nhập số lượng:");
        if (input == null || input.isEmpty()) {
            return;
        }
        Integer soLuong = Integer.parseInt(input);
        if (soLuong < 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải > 0");
            return;
        }

        Integer idHDCT = (Integer) tblGioHang.getValueAt(rowCart, 0);
        HoaDonChiTiet hdctBanDau = hoaDonCTService.selectById(idHDCT);
        SanPhamCT spctUpdate = service.selectById(hdctBanDau.getIdSP());

        Integer slspGioHang = (Integer) tblGioHang.getValueAt(rowCart, 7);

        if (soLuong > spctUpdate.getSoLuong()) {
            JOptionPane.showMessageDialog(this, "Sản phẩm chỉ còn lại " + spctUpdate.getSoLuong());
            soLuong = spctUpdate.getSoLuong() + slspGioHang;
        }
        this.updateCartAndProducr(soLuong);
        this.row = tblHoaDon.getSelectedRow();
        String maHD = (String) tblHoaDon.getValueAt(row, 1);
        HoaDon hoaDon = hdService.selectByMa(maHD);
        this.setDataHoaDon(hoaDon);
    }//GEN-LAST:event_btnSuaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFirst_Product;
    private javax.swing.JButton btnKhachHang;
    private javax.swing.JButton btnLast_Product;
    private javax.swing.JButton btnNext_Product;
    private javax.swing.JButton btnPrev_Product;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTaoHD;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblMaHD;
    private javax.swing.JLabel lblNgayMua;
    private javax.swing.JLabel lblPages;
    private javax.swing.JLabel lblTenKH;
    private javax.swing.JLabel lblTenNV;
    private javax.swing.JLabel lblTienThua;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JPanel pnlCam;
    private javax.swing.JTable tblGioHang;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTable tblSanPham;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTienTra;
    // End of variables declaration//GEN-END:variables
}
