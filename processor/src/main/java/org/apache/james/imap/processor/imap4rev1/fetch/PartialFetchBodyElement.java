package org.apache.james.imap.processor.imap4rev1.fetch;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import org.apache.james.imap.message.response.imap4rev1.FetchResponse.BodyElement;

/**
 * Wraps full content to implement a partial fetch.
 */
final class PartialFetchBodyElement implements BodyElement {

    private final BodyElement delegate;

    private final long firstOctet;

    private final long numberOfOctets;

    private final String name;

    public PartialFetchBodyElement(final BodyElement delegate,
            final long firstOctet, final long numberOfOctets) {
        super();
        this.delegate = delegate;
        this.firstOctet = firstOctet;
        this.numberOfOctets = numberOfOctets;
        name = delegate.getName() + "<" + firstOctet + ">";
    }

    public String getName() {
        return name;
    }

    public long size() {
        final long size = delegate.size();
        final long lastOctet = this.numberOfOctets + firstOctet;
        final long result;
        if (firstOctet > size) {
            result = 0;
        } else if (size > lastOctet) {
            result = numberOfOctets;
        } else {
            result = size - firstOctet;
        }
        return result;
    }

    public void writeTo(WritableByteChannel channel) throws IOException {
        PartialWritableByteChannel partialChannel = new PartialWritableByteChannel(
                channel, firstOctet, size());
        delegate.writeTo(partialChannel);
    }

}