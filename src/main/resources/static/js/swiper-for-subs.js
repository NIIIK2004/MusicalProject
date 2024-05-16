const swiper = new Swiper('.theme__swiper', {
    slidesPerView: 'auto',
    spaceBetween: 30,
    centeredSlides: true,
    loop: true,
    allowTouchMove: false,
    autoplay: {
        delay: -500,
        disableOnInteraction: false,
        reverseDirection: true
    },
    speed: 4000,
    direction: 'horizontal'
});

AOS.init();