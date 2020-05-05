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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
import org.reaktivity.specification.amqp.internal.types.AmqpCapabilities;
import org.reaktivity.specification.amqp.internal.types.AmqpMessagePropertyFW;
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

    public static class AmqpRouteExBuilder
    {
        private final AmqpRouteExFW.Builder routeExRW;

        public AmqpRouteExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[MAX_BUFFER_SIZE]);
            this.routeExRW = new AmqpRouteExFW.Builder()
                .wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public AmqpRouteExBuilder targetAddress(
            String targetAddress)
        {
            routeExRW.targetAddress(targetAddress);
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

        public AmqpDataExBuilder deliveryId(
            long deliveryId)
        {
            dataExRW.deliveryId(deliveryId);
            return this;
        }

        public AmqpDataExBuilder deliveryTag(
            String deliveryTag)
        {
            dataExRW.deliveryTag(d -> d.bytes(b -> b.set(deliveryTag.getBytes(StandardCharsets.UTF_8))));
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
            String value)
        {
            return key instanceof Long ? annotations((long) key, value) : annotations((String) key, value);
        }

        private AmqpDataExBuilder annotations(
            long key,
            String value)
        {
            dataExRW.annotationsItem(a -> a.key(k -> k.id(key))
                                           .value(v -> v.bytes(b -> b.set(value.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        private AmqpDataExBuilder annotations(
            String key,
            String value)
        {
            dataExRW.annotationsItem(a -> a.key(k -> k.name(key))
                                           .value(v -> v.bytes(b -> b.set(value.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        public AmqpDataExBuilder applicationProperty(
            String key,
            String value)
        {
            dataExRW.applicationPropertiesItem(a -> a.key(key)
                                                     .value(value));
            return this;
        }

        public AmqpDataExBuilder messageId(
            Object messageId)
        {
            if (messageId instanceof Long)
            {
                dataExRW.propertiesItem(p -> p.messageId(m -> m.ulong((long) messageId)));
                return this;
            }
            else if (messageId instanceof byte[])
            {
                dataExRW.propertiesItem(p -> p.messageId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) messageId)))));
                return this;
            }
            dataExRW.propertiesItem(p -> p.messageId(m -> m.stringtype((String) messageId)));
            return this;
        }

        public AmqpDataExBuilder userId(
            String userId)
        {
            dataExRW.propertiesItem(p ->
                p.userId(u -> u.bytes(b -> b.set(userId.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        public AmqpDataExBuilder to(
            String to)
        {
            dataExRW.propertiesItem(p -> p.to(to));
            return this;
        }

        public AmqpDataExBuilder subject(
            String subject)
        {
            dataExRW.propertiesItem(p -> p.subject(subject));
            return this;
        }

        public AmqpDataExBuilder replyTo(
            String replyTo)
        {
            dataExRW.propertiesItem(p -> p.replyTo(replyTo));
            return this;
        }

        public AmqpDataExBuilder correlationId(
            Object correlationId)
        {
            if (correlationId instanceof Long)
            {
                dataExRW.propertiesItem(p -> p.correlationId(m -> m.ulong((long) correlationId)));
                return this;
            }
            else if (correlationId instanceof byte[])
            {
                dataExRW.propertiesItem(p -> p.correlationId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) correlationId)))));
                return this;
            }
            dataExRW.propertiesItem(p -> p.correlationId(m -> m.stringtype((String) correlationId)));
            return this;
        }

        public AmqpDataExBuilder contentType(
            String contentType)
        {
            dataExRW.propertiesItem(p -> p.contentType(contentType));
            return this;
        }

        public AmqpDataExBuilder contentEncoding(
            String contentEncoding)
        {
            dataExRW.propertiesItem(p -> p.contentEncoding(contentEncoding));
            return this;
        }

        public AmqpDataExBuilder absoluteExpiryTime(
            long absoluteExpiryTime)
        {
            dataExRW.propertiesItem(p -> p.absoluteExpiryTime(absoluteExpiryTime));
            return this;
        }

        public AmqpDataExBuilder creationTime(
            long creationTime)
        {
            dataExRW.propertiesItem(p -> p.creationTime(creationTime));
            return this;
        }

        public AmqpDataExBuilder groupId(
            String groupId)
        {
            dataExRW.propertiesItem(p -> p.groupId(groupId));
            return this;
        }

        public AmqpDataExBuilder groupSequence(
            int groupSequence)
        {
            dataExRW.propertiesItem(p -> p.groupSequence(groupSequence));
            return this;
        }

        public AmqpDataExBuilder replyToGroupId(
            String replyToGroupId)
        {
            dataExRW.propertiesItem(p -> p.replyToGroupId(replyToGroupId));
            return this;
        }

        public byte[] build()
        {
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
        private Long deliveryId;
        private AmqpBinaryFW.Builder deliveryTagRW;
        private Long messageFormat;
        private Integer flags;
        private Array32FW.Builder<AmqpAnnotationFW.Builder, AmqpAnnotationFW> annotationsRW;
        private Array32FW.Builder<AmqpMessagePropertyFW.Builder, AmqpMessagePropertyFW> messagePropertiesRW;
        private Array32FW.Builder<AmqpApplicationPropertyFW.Builder, AmqpApplicationPropertyFW> applicationPropertiesRW;

        public AmqpDataExMatcherBuilder typeId(
            int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public AmqpDataExMatcherBuilder deliveryId(
            long deliveryId)
        {
            assert this.deliveryId == null;
            this.deliveryId = deliveryId;
            return this;
        }

        public AmqpDataExMatcherBuilder deliveryTag(
            String deliveryTag)
        {
            assert deliveryTagRW == null;
            deliveryTagRW = new AmqpBinaryFW.Builder().wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            deliveryTagRW.bytes(b -> b.set(deliveryTag.getBytes(StandardCharsets.UTF_8)));
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
            String value)
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
            String value)
        {
            annotationsRW.item(a -> a.key(k -> k.id(key))
                                     .value(v -> v.bytes(o -> o.set(value.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        private AmqpDataExMatcherBuilder annotations(
            String key,
            String value)
        {
            annotationsRW.item(a -> a.key(k -> k.name(key))
                                     .value(v -> v.bytes(b -> b.set(value.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        public AmqpDataExMatcherBuilder messageId(
            Object messageId)
        {
            initializeMessageProperties();
            if (messageId instanceof Long)
            {
                messagePropertiesRW.item(p -> p.messageId(m -> m.ulong((long) messageId)));
                return this;
            }
            else if (messageId instanceof byte[])
            {
                messagePropertiesRW.item(p -> p.messageId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) messageId)))));
                return this;
            }
            messagePropertiesRW.item(p -> p.messageId(m -> m.stringtype((String) messageId)));
            return this;
        }

        public AmqpDataExMatcherBuilder userId(
            String userId)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.userId(u -> u.bytes(b -> b.set(userId.getBytes(StandardCharsets.UTF_8)))));
            return this;
        }

        public AmqpDataExMatcherBuilder to(
            String to)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.to(to));
            return this;
        }

        public AmqpDataExMatcherBuilder subject(
            String subject)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.subject(subject));
            return this;
        }

        public AmqpDataExMatcherBuilder replyTo(
            String replyTo)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.replyTo(replyTo));
            return this;
        }

        public AmqpDataExMatcherBuilder correlationId(
            Object correlationId)
        {
            initializeMessageProperties();
            if (correlationId instanceof Long)
            {
                messagePropertiesRW.item(p -> p.correlationId(m -> m.ulong((long) correlationId)));
                return this;
            }
            else if (correlationId instanceof byte[])
            {
                messagePropertiesRW.item(p -> p.correlationId(m -> m.binary(b -> b.bytes(x -> x.set((byte[]) correlationId)))));
                return this;
            }
            messagePropertiesRW.item(p -> p.correlationId(m -> m.stringtype((String) correlationId)));
            return this;
        }

        public AmqpDataExMatcherBuilder contentType(
            String contentType)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.contentType(contentType));
            return this;
        }

        public AmqpDataExMatcherBuilder contentEncoding(
            String contentEncoding)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.contentEncoding(contentEncoding));
            return this;
        }

        public AmqpDataExMatcherBuilder absoluteExpiryTime(
            long absoluteExpiryTime)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.absoluteExpiryTime(absoluteExpiryTime));
            return this;
        }

        public AmqpDataExMatcherBuilder creationTime(
            long creationTime)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.creationTime(creationTime));
            return this;
        }

        public AmqpDataExMatcherBuilder groupId(
            String groupId)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.groupId(groupId));
            return this;
        }

        public AmqpDataExMatcherBuilder groupSequence(
            int groupSequence)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.groupSequence(groupSequence));
            return this;
        }

        public AmqpDataExMatcherBuilder replyToGroupId(
            String replyToGroupId)
        {
            initializeMessageProperties();
            messagePropertiesRW.item(p -> p.replyToGroupId(replyToGroupId));
            return this;
        }

        private void initializeMessageProperties()
        {
            if (messagePropertiesRW == null)
            {
                this.messagePropertiesRW = new  Array32FW.Builder<>(new AmqpMessagePropertyFW.Builder(),
                    new AmqpMessagePropertyFW())
                    .wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
        }

        public AmqpDataExMatcherBuilder applicationProperty(
            String key,
            String value)
        {
            if (applicationPropertiesRW == null)
            {
                this.applicationPropertiesRW = new Array32FW.Builder<>(new AmqpApplicationPropertyFW.Builder(),
                    new AmqpApplicationPropertyFW())
                    .wrap(new UnsafeBuffer(new byte[1024]), 0, 1024);
            }
            applicationPropertiesRW.item(a -> a.key(key).value(value));
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
                matchDeliveryId(dataEx) &&
                matchDeliveryTag(dataEx) &&
                matchMessageFormat(dataEx) &&
                matchFlags(dataEx) &&
                matchAnnotations(dataEx) &&
                matchProperties(dataEx) &&
                matchApplicationProperties(dataEx))
            {
                byteBuf.position(byteBuf.position() + dataEx.sizeof());
                return dataEx;
            }

            throw new Exception(dataEx.toString());
        }

        private boolean matchTypeId(
            final AmqpDataExFW dataEx)
        {
            return typeId == null || typeId == dataEx.typeId();
        }

        private boolean matchDeliveryId(
            final AmqpDataExFW dataEx)
        {
            return deliveryId == null || deliveryId == dataEx.deliveryId();
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
            return messagePropertiesRW == null || messagePropertiesRW.build().equals(dataEx.properties());
        }

        private boolean matchApplicationProperties(
            final AmqpDataExFW dataEx)
        {
            return applicationPropertiesRW == null || applicationPropertiesRW.build().equals(dataEx.applicationProperties());
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
