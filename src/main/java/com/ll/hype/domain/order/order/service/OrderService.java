package com.ll.hype.domain.order.order.service;

import com.ll.hype.domain.member.member.entity.Member;
import com.ll.hype.domain.order.buy.dto.request.CreateBuyRequest;
import com.ll.hype.domain.order.buy.entity.Buy;
import com.ll.hype.domain.order.buy.repository.BuyRepository;
import com.ll.hype.domain.order.order.dto.OrderRequest;
import com.ll.hype.domain.order.order.dto.response.OrderResponse;
import com.ll.hype.domain.order.order.entity.OrderStatus;
import com.ll.hype.domain.order.order.entity.Orders;
import com.ll.hype.domain.order.order.entity.PaymentStatus;
import com.ll.hype.domain.order.order.repository.OrderRepository;
import com.ll.hype.domain.order.sale.dto.request.CreateSaleRequest;
import com.ll.hype.domain.order.sale.dto.response.SaleResponse;
import com.ll.hype.domain.order.sale.entity.Sale;
import com.ll.hype.domain.order.sale.repository.SaleRepository;
import java.time.LocalDate;
import java.util.List;

import com.ll.hype.domain.shoes.shoes.entity.Shoes;
import com.ll.hype.domain.shoes.shoes.repository.ShoesRepository;
import com.ll.hype.global.s3.image.ImageType;
import com.ll.hype.global.s3.image.imagebridge.component.ImageBridgeComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BuyRepository buyRepository;
    private final SaleRepository saleRepository;
    private final ShoesRepository shoesRepository;

    private final ImageBridgeComponent imageBridgeComponent;

    // 거래 체결
    public Object createOrder(Buy buy, Sale sale) {
        return null;
    }


    // 환불로 인한 거래 취소
    public Object refundCancelOrder(Orders orders) {
        return null;
    }

    // 미입금으로 인한 거래 취소
    public Object nonDepositCancelOrder(Orders orders) {
        return null;
    }

    // TODO
    // + 금액, 모델명, 사이즈가 다르면 거래불가
    // + !! buyResponse 주문 저장되면 status 변경

    public OrderResponse createOrder(OrderRequest orderRequest, SaleResponse saleResponse, Member member) {
        Shoes shoes = shoesRepository.findById(saleResponse.getShoes().getId())
                .orElseThrow(() -> new IllegalArgumentException("조회된 신발이 없습니다."));

        Buy buy = buyRepository.findHighestPriceBuy(shoes, saleResponse.getShoesSize().getSize()) //orderRequest.getBuy().getId()
                .orElseThrow(() -> new IllegalArgumentException("조회된 구매 입찰이 없습니다."));

        Sale sale = saleRepository.findById(saleResponse.getId())
                .orElseThrow(() -> new IllegalArgumentException("조회된 판매 입찰이 없습니다."));

        List<String> fullPath = imageBridgeComponent.findOneFullPath(ImageType.SHOES, saleResponse.getShoes().getId());

//내가 판매할건데 이미 구매입찰 올라와있는 이 가격에 팔겠다 orderPrice=buy.getPrice
//        if (!buy.getPrice().equals(sale.getPrice())) {
//            throw new IllegalArgumentException("거래 성사 금액이 일치하지 않습니다.");
//        }

        Orders order = Orders.builder()
                .buy(buy)
                .sale(sale)
                .orderDate(LocalDate.now())
                .orderPrice(buy.getPrice())
                .receiverName(buy.getReceiverName())
                .receiverPhoneNumber(buy.getReceiverPhoneNumber())
                .receiverAddress(buy.getReceiverAddress())
                .status(OrderStatus.TRADING)
                .paymentStatus(PaymentStatus.WAIT_PAYMENT)
                .build();
        orderRepository.save(order);

//        order.updateTossId(order.createTossId());
        return OrderResponse.of(order, fullPath);
    }

    public void checkAmount(String tossId, String amountStr) {
        Orders order = orderRepository.findByTossId(tossId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 주문이 없습니다."));

        long amount = Long.parseLong(amountStr);

        log.info("[OrderService.checkAmount] amount : " + amount);
        log.info("[OrderService.checkAmount] Order amount : " + order.getOrderPrice());

        if (amount != order.getOrderPrice()) {
            throw new IllegalArgumentException("주문금액과 결제금액이 일치하지 않습니다.");
        }
    }

    @Transactional
    public void setPaymentComplete(String tossId) {
        Orders order = orderRepository.findByTossId(tossId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 주문이 없습니다."));
        log.info("[OrderService.setPaymentComplete] 여기까지는 오나?");
        order.updatePaymentStatus(PaymentStatus.COMPLETE_PAYMENT);
    }
}
