package com.ecommerce.model;

public enum OrderStatus {
    REALIZADO,   // Estado inicial cuando se crea el pedido
    PAGADO,      // Admin confirma el pago
    ENVIADO,     // Admin marca como enviado
    RECIBIDO     // Cliente confirma recepción
} 