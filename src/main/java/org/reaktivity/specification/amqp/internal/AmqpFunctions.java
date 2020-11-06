/**
 * Copyright 2016-2020 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.amqp.internal;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.reaktivity.specification.amqp.internal.types.AmqpAnnotationFW;
import org.reaktivity.specification.amqp.internal.types.AmqpApplicationPropertyFW;
import org.reaktivity.specification.amqp.internal.types.AmqpBinaryFW;
import org.reaktivity.specification.amqp.internal.types.AmqpBodyKind;
import org.reaktivity.specification.amqp.internal.types.AmqpCapabilities;
import org.reaktivity.specification.amqp.internal.types.AmqpPropertiesFW;
import org.reaktivity.specification.amqp.internal.types.AmqpReceiverSettleMode;
import org.reaktivity.specification.amqp.internal.types.AmqpSenderSettleMode;
import org.reaktivity.specification.amqp.internal.types.AmqpTransferFlag;
import org.reaktivity.specification.amqp.internal.types.Array32FW;
import org.reaktivity.specification.amqp.internal.types.control.AmqpRouteExFW;
import org.reaktivity.specification.amqp.internal.types.stream.AmqpAbortExFW;
import org.reaktivity.specification.amqp.internal.types.stream.AmqpBeginExFW;
import org.reaktivity.specification.amqp.internal.types.stream.AmqpDataExFW;

public final class AmqpFunctions
{
    private static final int MAX_BUFFER_SIZE = 1024 * 8;

    private static final byte[] NULL_TYPE = new byte[] {0x40};
    private static final byte BOOLEAN_TYPE = (byte) 0x56;
    private static final byte[] TRUE_TYPE = new byte[] {0x41};
    private static final byte[] FALSE_TYPE = new byte[] {0x42};
    private static final byte UBYTE_TYPE = (byte) 0x50;
    private static final byte USHORT_TYPE = (byte) 0x60;
    private static final byte UINT_TYPE = (byte) 0x70;
    private static final byte SMALLUINT_TYPE = (byte) 0x52;
    private static final byte[] UINT0_TYPE = new byte[] {0x43};
    private static final byte ULONG_TYPE = (byte) 0x80;
    private static final byte SMALLULONG_TYPE = (byte) 0x53;
    private static final byte[] ULONG0_TYPE = new byte[] {0x44};
    private static final byte BYTE_TYPE = (byte) 0x51;
    private static final byte SHORT_TYPE = (byte) 0x61;
    private static final byte INT_TYPE = (byte) 0x71;
    private static final byte SMALLINT_TYPE = (byte) 0x54;
    private static final byte LONG_TYPE = (byte) 0x81;
    private static final byte SMALLLONG_TYPE = (byte) 0x55;
    private static final byte CHAR_TYPE = (byte) 0x73;
    private static final byte TIMESTAMP_TYPE = (byte) 0x83;

    private static final byte VBIN8_TYPE = (byte) 0xa0;
    private static final byte VBIN32_TYPE = (byte) 0xb0;
    private static final byte STR8UTF8_TYPE = (byte) 0xa1;
    private static final byte STR32UTF8_TYPE = (byte) 0xb1;
    private static final byte SYM8_TYPE = (byte) 0xa3;
    private static final byte SYM32_TYPE = (byte) 0xb3;

    private static final int CONSTRUCTOR_BYTE_SIZE = 1;
    private static final int FIXED_SIZE1 = 1;
    private static final int FIXED_SIZE2 = 2;
    private static final int FIXED_SIZE4 = 4;
    private static final int FIXED_SIZE8 = 8;

    public static class AmqpRouteExBuilder
    {
        private final AmqpRouteExFW.Builder routeExRW;

        public AmqpRouteExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_BUFFER_SIZE]);
            this.routeExRW = new AmqpRouteExFW.Builder()
                .wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public AmqpRouteExBuilder address(
            String address)
        {
            routeExRW.address(address);
            return this;
        }

        public AmqpRouteExBuilder capabilities(
            String capabilities)
        {
            routeExRW.capabilities(r -> r.set(AmqpCapabilities.valueOf(capabilities)));
            return this;
        }

        public byte[] build()
        {
            final AmqpRouteExFW amqpRouteEx = routeExRW.build();
            final byte[] result = new byte[amqpRouteEx.sizeof()];
            amqpRouteEx.buffer().getBytes(0, result);
            return result;
        }
    }

    public static class AmqpBeginExBuilder
    {
        private final AmqpBeginExFW.Builder beginExRW;

        public AmqpBeginExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_BUFFER_SIZE]);
            this.beginExRW = new AmqpBeginExFW.Builder()
                .wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public AmqpBeginExBuilder typeId(
            int typeId)
        {
            beginExRW.typeId(typeId);
            return this;
        }

        public AmqpBeginExBuilder address(
            String address)
        {
            beginExRW.address(address);
            return this;
        }

        public AmqpBeginExBuilder capabilities(
            String capabilities)
        {
            beginExRW.capabilities(r -> r.set(AmqpCapabilities.valueOf(capabilities)));
            return this;
        }

        public AmqpBeginExBuilder senderSettleMode(
            String senderSettleMode)
        {
            beginExRW.senderSettleMode(s -> s.set(AmqpSenderSettleMode.valueOf(senderSettleMode)));
            return this;
        }

        public AmqpBeginExBuilder receiverSettleMode(
            String receiverSettleMode)
        {
            beginExRW.receiverSettleMode(r -> r.set(AmqpReceiverSettleMode.valueOf(receiverSettleMode)));
            return this;
        }

        public byte[] build()
        {
            final AmqpBeginExFW amqpBeginEx = beginExRW.build();
            final byte[] result = new byte[amqpBeginEx.sizeof()];
            amqpBeginEx.buffer().getBytes(0, result);
            return result;
        }
    }

    public static class AmqpDataExBuilder
    {
        private final AmqpDataExFW.Builder dataExRW;
        private AmqpPropertiesFW.Builder propertiesRW;
        private boolean isPropertiesSet;

        public AmqpDataExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_BUFFER_SIZE]);
            this.dataExRW = new AmqpDataExFW.Builder()
                .wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public AmqpDataExBuilder typeId(
            int typeId)
        {
            dataExRW.typeId(typeId);
            return this;
        }

        public AmqpDataExBuilder deferred(
            int deferred)
        {
            dataExRW.deferred(deferred);
            return this;
        }

        public AmqpDataExBuilder deliveryTag(
            String deliveryTag)
        {
            dataExRW.deliveryTag(d -> d.bytes(b -> b.set(deliveryTag.getBytes(UTF_8))));
            return this;
        }

        public AmqpDataExBuilder messageFormat(
            long messageFormat)
        {
            dataExRW.messageFormat(messageFormat);
            return this;
        }

        public AmqpDataExBuilder flags(
            String... flags)
        {
            int value = 0;
            for (String flag : flags)
            {
                AmqpTransferFlag transferFlag = AmqpTransferFlag.valueOf(flag);
                switch (transferFlag)
                {
                case SETTLED:
                    value |= 1;
                    break;
                case RESUME:
                    value |= 2;
                    break;
                case ABORTED:
                    value |= 4;
                    break;
                case BATCHABLE:
                    value |= 8;
                    break;
                }
            }
            dataExRW.flags(value);
            return this;
        }

        public AmqpDataExBuilder annotation(
            Object key,
            byte[] value)
        {
            return key instanceof Long ? annotations((long) key, value) : annotations((String) key, value);
        }

        private AmqpDataExBuilder annotations(
            long key,
            byte[] value)
        {
            dataExRW.annotationsItem(a -> a.key(k -> k.id(key))
                                           .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        private AmqpDataExBuilder annotations(
            String key,
            byte[] value)
        {
            dataExRW.annotationsItem(a -> a.key(k -> k.name(key))
                                           .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        public AmqpDataExBuilder messageId(
            Object messageId)
        {
            AmqpPropertiesFW.Builder properties = properties();
            if (messageId instanceof Long)
            {
                properties.messageId(m -> m.ulong((long) messageId));
                return this;
            }
            else if (messageId instanceof byte[])
            {
                properties.messageId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) messageId))));
                return this;
            }
            properties.messageId(m -> m.stringtype((String) messageId));
            return this;
        }

        public AmqpDataExBuilder userId(
            String userId)
        {
            properties().userId(u -> u.bytes(b -> b.set(userId.getBytes(UTF_8))));
            return this;
        }

        public AmqpDataExBuilder to(
            String to)
        {
            properties().to(to);
            return this;
        }

        public AmqpDataExBuilder subject(
            String subject)
        {
            properties().subject(subject);
            return this;
        }

        public AmqpDataExBuilder replyTo(
            String replyTo)
        {
            properties().replyTo(replyTo);
            return this;
        }

        public AmqpDataExBuilder correlationId(
            Object correlationId)
        {
            AmqpPropertiesFW.Builder properties = properties();
            if (correlationId instanceof Long)
            {
                properties.correlationId(m -> m.ulong((long) correlationId));
                return this;
            }
            else if (correlationId instanceof byte[])
            {
                properties.correlationId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) correlationId))));
                return this;
            }
            properties.correlationId(m -> m.stringtype((String) correlationId));
            return this;
        }

        public AmqpDataExBuilder contentType(
            String contentType)
        {
            properties().contentType(contentType);
            return this;
        }

        public AmqpDataExBuilder contentEncoding(
            String contentEncoding)
        {
            properties().contentEncoding(contentEncoding);
            return this;
        }

        public AmqpDataExBuilder absoluteExpiryTime(
            long absoluteExpiryTime)
        {
            properties().absoluteExpiryTime(absoluteExpiryTime);
            return this;
        }

        public AmqpDataExBuilder creationTime(
            long creationTime)
        {
            properties().creationTime(creationTime);
            return this;
        }

        public AmqpDataExBuilder groupId(
            String groupId)
        {
            properties().groupId(groupId);
            return this;
        }

        public AmqpDataExBuilder groupSequence(
            int groupSequence)
        {
            properties().groupSequence(groupSequence);
            return this;
        }

        public AmqpDataExBuilder replyToGroupId(
            String replyToGroupId)
        {
            properties().replyToGroupId(replyToGroupId);
            return this;
        }

        public AmqpDataExBuilder property(
            String key,
            byte[] value)
        {
            if (propertiesRW != null && !isPropertiesSet)
            {
                final AmqpPropertiesFW properties = propertiesRW.build();
                dataExRW.properties(properties);
                isPropertiesSet = true;
            }
            dataExRW.applicationPropertiesItem(a -> a.key(key)
                                                     .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        private AmqpPropertiesFW.Builder properties()
        {
            if (propertiesRW == null)
            {
                propertiesRW = new AmqpPropertiesFW.Builder().wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
            return propertiesRW;
        }

        public AmqpDataExBuilder bodyKind(
            String bodyKind)
        {
            if (propertiesRW != null && !isPropertiesSet)
            {
                final AmqpPropertiesFW properties = propertiesRW.build();
                dataExRW.properties(properties);
                isPropertiesSet = true;
            }
            dataExRW.bodyKind(b -> b.set(AmqpBodyKind.valueOf(bodyKind)));
            return this;
        }

        public byte[] build()
        {
            if (propertiesRW != null && !isPropertiesSet)
            {
                final AmqpPropertiesFW properties = propertiesRW.build();
                dataExRW.properties(properties);
            }
            final AmqpDataExFW amqpDataEx = dataExRW.build();
            final byte[] result = new byte[amqpDataEx.sizeof()];
            amqpDataEx.buffer().getBytes(0, result);
            return result;
        }
    }

    public static final class AmqpDataExMatcherBuilder
    {
        private final DirectBuffer bufferRO = new UnsafeBuffer();

        private final AmqpDataExFW dataExRO = new AmqpDataExFW();

        private Integer typeId;
        private Integer deferred;
        private AmqpBinaryFW.Builder deliveryTagRW;
        private Long messageFormat;
        private Integer flags;
        private String bodyKind;
        private Array32FW.Builder<AmqpAnnotationFW.Builder, AmqpAnnotationFW> annotationsRW;
        private AmqpPropertiesFW.Builder propertiesRW;
        private Array32FW.Builder<AmqpApplicationPropertyFW.Builder, AmqpApplicationPropertyFW> applicationPropertiesRW;

        public AmqpDataExMatcherBuilder typeId(
            int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public AmqpDataExMatcherBuilder deferred(
            int deferred)
        {
            this.deferred = deferred;
            return this;
        }

        public AmqpDataExMatcherBuilder deliveryTag(
            String deliveryTag)
        {
            assert deliveryTagRW == null;
            deliveryTagRW = new AmqpBinaryFW.Builder().wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            deliveryTagRW.bytes(b -> b.set(deliveryTag.getBytes(UTF_8)));
            return this;
        }

        public AmqpDataExMatcherBuilder messageFormat(
            long messageFormat)
        {
            assert this.messageFormat == null;
            this.messageFormat = messageFormat;
            return this;
        }

        public AmqpDataExMatcherBuilder flags(
            String... flags)
        {
            assert this.flags == null;
            int value = 0;
            for (String flag : flags)
            {
                AmqpTransferFlag transferFlag = AmqpTransferFlag.valueOf(flag);
                switch (transferFlag)
                {
                case SETTLED:
                    value |= 1;
                    break;
                case RESUME:
                    value |= 2;
                    break;
                case ABORTED:
                    value |= 4;
                    break;
                case BATCHABLE:
                    value |= 8;
                    break;
                }
            }
            this.flags = value;
            return this;
        }

        public AmqpDataExMatcherBuilder annotation(
            Object key,
            byte[] value)
        {
            if (annotationsRW == null)
            {
                this.annotationsRW = new Array32FW.Builder<>(new AmqpAnnotationFW.Builder(), new AmqpAnnotationFW())
                    .wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
            return key instanceof Long ? annotations((long) key, value) : annotations((String) key, value);
        }

        private AmqpDataExMatcherBuilder annotations(
            long key,
            byte[] value)
        {
            annotationsRW.item(a -> a.key(k -> k.id(key))
                                     .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        private AmqpDataExMatcherBuilder annotations(
            String key,
            byte[] value)
        {
            annotationsRW.item(a -> a.key(k -> k.name(key))
                                     .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        public AmqpDataExMatcherBuilder messageId(
            Object messageId)
        {
            AmqpPropertiesFW.Builder properties = properties();
            if (messageId instanceof Long)
            {
                properties.messageId(m -> m.ulong((long) messageId));
                return this;
            }
            else if (messageId instanceof byte[])
            {
                properties.messageId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) messageId))));
                return this;
            }
            properties.messageId(m -> m.stringtype((String) messageId));
            return this;
        }

        public AmqpDataExMatcherBuilder userId(
            String userId)
        {
            properties().userId(u -> u.bytes(b -> b.set(userId.getBytes(UTF_8))));
            return this;
        }

        public AmqpDataExMatcherBuilder to(
            String to)
        {
            properties().to(to);
            return this;
        }

        public AmqpDataExMatcherBuilder subject(
            String subject)
        {
            properties().subject(subject);
            return this;
        }

        public AmqpDataExMatcherBuilder replyTo(
            String replyTo)
        {
            properties().replyTo(replyTo);
            return this;
        }

        public AmqpDataExMatcherBuilder correlationId(
            Object correlationId)
        {
            AmqpPropertiesFW.Builder properties = properties();
            if (correlationId instanceof Long)
            {
                properties.correlationId(m -> m.ulong((long) correlationId));
                return this;
            }
            else if (correlationId instanceof byte[])
            {
                properties.correlationId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) correlationId))));
                return this;
            }
            properties.correlationId(m -> m.stringtype((String) correlationId));
            return this;
        }

        public AmqpDataExMatcherBuilder contentType(
            String contentType)
        {
            properties().contentType(contentType);
            return this;
        }

        public AmqpDataExMatcherBuilder contentEncoding(
            String contentEncoding)
        {
            properties().contentEncoding(contentEncoding);
            return this;
        }

        public AmqpDataExMatcherBuilder absoluteExpiryTime(
            long absoluteExpiryTime)
        {
            properties().absoluteExpiryTime(absoluteExpiryTime);
            return this;
        }

        public AmqpDataExMatcherBuilder creationTime(
            long creationTime)
        {
            properties().creationTime(creationTime);
            return this;
        }

        public AmqpDataExMatcherBuilder groupId(
            String groupId)
        {
            properties().groupId(groupId);
            return this;
        }

        public AmqpDataExMatcherBuilder groupSequence(
            int groupSequence)
        {
            properties().groupSequence(groupSequence);
            return this;
        }

        public AmqpDataExMatcherBuilder replyToGroupId(
            String replyToGroupId)
        {
            properties().replyToGroupId(replyToGroupId);
            return this;
        }

        private AmqpPropertiesFW.Builder properties()
        {
            if (propertiesRW == null)
            {
                propertiesRW = new AmqpPropertiesFW.Builder().wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
            return propertiesRW;
        }

        public AmqpDataExMatcherBuilder property(
            String key,
            byte[] value)
        {
            if (applicationPropertiesRW == null)
            {
                this.applicationPropertiesRW = new Array32FW.Builder<>(new AmqpApplicationPropertyFW.Builder(),
                    new AmqpApplicationPropertyFW())
                    .wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
            applicationPropertiesRW.item(a -> a.key(key)
                                               .value(v -> v.bytes(o -> o.set(value))));
            return this;
        }

        public AmqpDataExMatcherBuilder bodyKind(
            String bodyKind)
        {
            assert this.bodyKind == null;
            this.bodyKind = bodyKind;
            return this;
        }

        public BytesMatcher build()
        {
            return typeId != null ? this::match : buf -> null;
        }

        private AmqpDataExFW match(
            ByteBuffer byteBuf) throws Exception
        {
            bufferRO.wrap(byteBuf);
            final AmqpDataExFW dataEx = dataExRO.tryWrap(bufferRO, byteBuf.position(), byteBuf.capacity());

            if (dataEx != null &&
                matchTypeId(dataEx) &&
                matchDeferred(dataEx) &&
                matchDeliveryTag(dataEx) &&
                matchMessageFormat(dataEx) &&
                matchFlags(dataEx) &&
                matchAnnotations(dataEx) &&
                matchProperties(dataEx) &&
                matchApplicationProperties(dataEx) &&
                matchBodyKind(dataEx))
            {
                byteBuf.position(byteBuf.position() + dataEx.sizeof());
                return dataEx;
            }

            throw new Exception(dataEx.toString());
        }

        private boolean matchTypeId(
            final AmqpDataExFW dataEx)
        {
            return typeId == dataEx.typeId();
        }

        private boolean matchDeferred(
            final AmqpDataExFW dataEx)
        {
            return deferred == null || deferred == dataEx.deferred();
        }

        private boolean matchDeliveryTag(
            final AmqpDataExFW dataEx)
        {
            return deliveryTagRW == null || deliveryTagRW.build().equals(dataEx.deliveryTag());
        }

        private boolean matchMessageFormat(
            final AmqpDataExFW dataEx)
        {
            return messageFormat == null || messageFormat == dataEx.messageFormat();
        }

        private boolean matchFlags(
            final AmqpDataExFW dataEx)
        {
            return flags == null || flags == dataEx.flags();
        }

        private boolean matchAnnotations(
            final AmqpDataExFW dataEx)
        {
            return annotationsRW == null || annotationsRW.build().equals(dataEx.annotations());
        }

        private boolean matchProperties(
            final AmqpDataExFW dataEx)
        {
            return propertiesRW == null || propertiesRW.build().equals(dataEx.properties());
        }

        private boolean matchApplicationProperties(
            final AmqpDataExFW dataEx)
        {
            return applicationPropertiesRW == null || applicationPropertiesRW.build().equals(dataEx.applicationProperties());
        }

        private boolean matchBodyKind(
            final AmqpDataExFW dataEx)
        {
            return bodyKind == null || bodyKind.equals(dataEx.bodyKind().get().name());
        }
    }

    public static class AmqpAbortExBuilder
    {
        private final AmqpAbortExFW.Builder abortExRW;

        public AmqpAbortExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_BUFFER_SIZE]);
            this.abortExRW = new AmqpAbortExFW.Builder()
                .wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public AmqpAbortExBuilder typeId(
            int typeId)
        {
            abortExRW.typeId(typeId);
            return this;
        }

        public AmqpAbortExBuilder condition(
            String condition)
        {
            abortExRW.condition(condition);
            return this;
        }

        public byte[] build()
        {
            final AmqpAbortExFW amqpAbortEx = abortExRW.build();
            final byte[] result = new byte[amqpAbortEx.sizeof()];
            amqpAbortEx.buffer().getBytes(0, result);
            return result;
        }
    }

    @Function
    public static AmqpRouteExBuilder routeEx()
    {
        return new AmqpRouteExBuilder();
    }

    @Function
    public static AmqpBeginExBuilder beginEx()
    {
        return new AmqpBeginExBuilder();
    }

    @Function
    public static AmqpDataExBuilder dataEx()
    {
        return new AmqpDataExBuilder();
    }

    @Function
    public static AmqpDataExMatcherBuilder matchDataEx()
    {
        return new AmqpDataExMatcherBuilder();
    }

    @Function
    public static AmqpAbortExBuilder abortEx()
    {
        return new AmqpAbortExBuilder();
    }

    @Function
    public static byte[] randomBytes(
        int length)
    {
        Random random = ThreadLocalRandom.current();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
        {
            bytes[i] = (byte) random.nextInt(0x100);
        }
        return bytes;
    }

    @Function
    public static String randomString(
        int length)
    {
        int leftLimit = 97;
        int rightLimit = 122;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @Function(name = "_null")
    public static byte[] nullValue()
    {
        return NULL_TYPE;
    }

    @Function(name = "boolean")
    public static byte[] booleanValue(
        boolean value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        byte byteValue = (byte) (value ? 0x01 : 0x00);
        return ByteBuffer.allocate(byteLength).put(BOOLEAN_TYPE).put(byteValue).array();
    }

    @Function(name = "_true")
    public static byte[] trueValue()
    {
        return TRUE_TYPE;
    }

    @Function(name = "_false")
    public static byte[] falseValue()
    {
        return FALSE_TYPE;
    }

    @Function
    public static byte[] ubyte(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(UBYTE_TYPE).put((byte) value).array();
    }

    @Function
    public static byte[] ushort(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE2;
        return ByteBuffer.allocate(byteLength).put(USHORT_TYPE).putShort((short) value).array();
    }

    @Function
    public static byte[] uint(
        long value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4;
        return ByteBuffer.allocate(byteLength).put(UINT_TYPE).putInt((int) value).array();
    }

    @Function
    public static byte[] smalluint(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(SMALLUINT_TYPE).put((byte) value).array();
    }

    @Function
    public static byte[] uint0()
    {
        return UINT0_TYPE;
    }

    @Function
    public static byte[] ulong(
        long value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE8;
        return ByteBuffer.allocate(byteLength).put(ULONG_TYPE).putLong(value).array();
    }

    @Function
    public static byte[] smallulong(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(SMALLULONG_TYPE).put((byte) value).array();
    }

    @Function
    public static byte[] ulong0()
    {
        return ULONG0_TYPE;
    }

    @Function(name = "byte")
    public static byte[] byteValue(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(BYTE_TYPE).put((byte) value).array();
    }

    @Function(name = "short")
    public static byte[] shortValue(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE2;
        return ByteBuffer.allocate(byteLength).put(SHORT_TYPE).putShort((short) value).array();
    }

    @Function(name = "int")
    public static byte[] intValue(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4;
        return ByteBuffer.allocate(byteLength).put(INT_TYPE).putInt(value).array();
    }

    @Function
    public static byte[] smallint(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(SMALLINT_TYPE).put((byte) value).array();
    }

    @Function(name = "long")
    public static byte[] longValue(
        long value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE8;
        return ByteBuffer.allocate(byteLength).put(LONG_TYPE).putLong(value).array();
    }

    @Function
    public static byte[] smalllong(
        int value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1;
        return ByteBuffer.allocate(byteLength).put(SMALLLONG_TYPE).put((byte) value).array();
    }

    @Function(name = "char")
    public static byte[] charValue(
        String value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4;
        return ByteBuffer.allocate(byteLength).put(CHAR_TYPE).putShort((short) 0).put(value.getBytes(UTF_16BE)).array();
    }

    @Function
    public static byte[] timestamp(
        long value)
    {
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE8;
        return ByteBuffer.allocate(byteLength).put(TIMESTAMP_TYPE).putLong(value).array();
    }

    @Function
    public static byte[] binary8(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1 + length;

        return ByteBuffer.allocate(byteLength).put(VBIN8_TYPE).put((byte) length).put(value.getBytes(UTF_8)).array();
    }

    @Function
    public static byte[] binary32(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4 + length;

        return ByteBuffer.allocate(byteLength).put(VBIN32_TYPE).putInt(length).put(value.getBytes(UTF_8)).array();
    }

    @Function
    public static byte[] string8(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1 + length;

        return ByteBuffer.allocate(byteLength).put(STR8UTF8_TYPE).put((byte) length).put(value.getBytes(UTF_8)).array();
    }

    @Function
    public static byte[] string32(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4 + length;

        return ByteBuffer.allocate(byteLength).put(STR32UTF8_TYPE).putInt(length).put(value.getBytes(UTF_8)).array();
    }

    @Function
    public static byte[] symbol8(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE1 + length;

        return ByteBuffer.allocate(byteLength).put(SYM8_TYPE).put((byte) length).put(value.getBytes(UTF_8)).array();
    }

    @Function
    public static byte[] symbol32(
        String value)
    {
        int length = value.length();
        int byteLength = CONSTRUCTOR_BYTE_SIZE + FIXED_SIZE4 + length;

        return ByteBuffer.allocate(byteLength).put(SYM32_TYPE).putInt(length).put(value.getBytes(UTF_8)).array();
    }

    public static class Mapper extends FunctionMapperSpi.Reflective
    {
        public Mapper()
        {
            super(AmqpFunctions.class);
        }

        @Override
        public String getPrefixName()
        {
            return "amqp";
        }
    }

    private AmqpFunctions()
    {
        // utility
    }
}
