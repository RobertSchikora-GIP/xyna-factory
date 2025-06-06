/*
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 * Copyright 2022 Xyna GmbH, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 */
package xact.ssh;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.schmizz.sshj.userauth.keyprovider.KeyProvider;


public interface XynaIdentityRepository {
  
  public Collection<IdentityStorable> getAllIdentities();
  
  public List<KeyProvider> getAllKeys();
  
  public List<KeyProvider> getKey(String name, Optional<String> type);
  
  public KeyProvider storableToKeyProvider(IdentityStorable identity);
  
  public IdentityStorable add(Optional<String> name, EncryptionType keyType, byte[] privateKey, byte[] publicKey, Optional<String> passphrase);
  
  public Collection<IdentityStorable> removeKey(EncryptionType type, Optional<String> publickey);
  
  public void clearAll();
  
  public void init();
  
  public void shutdown();

  
  
}
