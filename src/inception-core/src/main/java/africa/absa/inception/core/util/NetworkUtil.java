/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.absa.inception.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * The <b>NetworkUtil</b> class is a utility class which provides network-related utility methods.
 *
 * @author Marcus Portmann
 */
public class NetworkUtil {

  /**
   * Returns an <b>InetAddress</b> object encapsulating what is most likely the machine's LAN IP
   * address.
   *
   * <p>This method is intended for use as a replacement of JDK method <b>
   * InetAddress.getLocalHost</b>, because that method is ambiguous on Linux systems. Linux systems
   * enumerate the loopback network interface the same way as regular LAN network interfaces, but
   * the JDK <b>InetAddress.getLocalHost</b> method does not specify the algorithm used to select
   * the address returned under such circumstances, and will often return the loopback address,
   * which is not valid for network communication. Details <a
   * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
   *
   * <p>This method will scan all IP addresses on all network interfaces on the host machine to
   * determine the IP address most likely to be the machine's LAN address. If the machine has
   * multiple IP addresses, this method will prefer a site-local IP address (e.g. 192.168.x.x or
   * 10.10.x.x, usually IPv4) if the machine has one (and will return the first site-local address
   * if the machine has more than one), but if the machine does not hold a site-local address, this
   * method will return simply the first non-loopback address found (IPv4 or IPv6).
   *
   * <p>If this method cannot find a non-loopback address using this selection algorithm, it will
   * fall back to calling and returning the result of JDK method <b>InetAddress.getLocalHost</b>.
   *
   * @return the LAN IP address
   * @throws UnknownHostException the LAN IP address could not be retrieved
   */
  public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
    try {
      InetAddress candidateAddress = null;

      // Iterate all NICs (network interface cards)
      for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
          networkInterfaces.hasMoreElements(); ) {
        NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();

        // Iterate all IP addresses assigned to each card
        for (Enumeration inetAddresses = networkInterface.getInetAddresses();
            inetAddresses.hasMoreElements(); ) {
          InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            if (inetAddress.isSiteLocalAddress()) {
              // Found non-loopback site-local address. Return it immediately
              return inetAddress;
            } else if (candidateAddress == null) {
              // Found non-loopback address, but not necessarily site-local.
              // Store it as a candidate to be returned if site-local address is not subsequently
              // found...
              candidateAddress = inetAddress;

              // Note that we don't repeatedly assign non-loopback non-site-local addresses as
              // candidates,
              // only the first. For subsequent iterations, candidate will be non-null.
            }
          }
        }
      }

      if (candidateAddress != null) {
        // We did not find a site-local address, but we found some other non-loopback address.
        // Server might have a non-site-local address assigned to its NIC (or it might be running
        // IPv6 which deprecates the "site-local" concept).
        // Return this non-loopback candidate address
        return candidateAddress;
      }

      // At this point, we did not find a non-loopback address.
      // Fall back to returning whatever InetAddress.getLocalHost() returns...
      InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
      if (jdkSuppliedAddress == null) {
        throw new UnknownHostException(
            "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
      }

      return jdkSuppliedAddress;
    } catch (Throwable e) {
      UnknownHostException unknownHostException =
          new UnknownHostException("Failed to determine LAN address: " + e);
      unknownHostException.initCause(e);

      throw unknownHostException;
    }
  }
}
